package com.example.makingnotes.ui;

import android.content.Context;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makingnotes.MainActivity;
import com.example.makingnotes.data.NoteSourceFirebaseImpl;
import com.example.makingnotes.data.NoteSourceInt;
import com.example.makingnotes.R;
import com.example.makingnotes.observe.Publisher;

import java.util.Objects;


public class ListOfNoteBlankFragment extends Fragment {
private NoteSourceInt data;

    private NoteAdapter adapter;
    private RecyclerView recyclerView;
    private Navigation navigation;
    private Publisher publisher;
    private boolean moveToLastPosition;

    public static ListOfNoteBlankFragment newInstance() {
        return new ListOfNoteBlankFragment();
    }

    // При создании фрагмента укажем макет
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_of_note_blank, container, false);
        recyclerView = view.findViewById(R.id.notes_recycler_view);
        data = new NoteSourceFirebaseImpl().init(notesData -> adapter.notifyDataSetChanged());
        initRecyclerView(recyclerView, data);
        setHasOptionsMenu(true);
        adapter.setDataSource(data);
        return  view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity) context;
        navigation = activity.getNavigation();
        publisher = activity.getPublisher();
    }

    @Override
    public void onDetach() {
        navigation = null;
        publisher = null;
        super.onDetach();
    }

    private void initRecyclerView(RecyclerView recyclerView, NoteSourceInt data) {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        if (moveToLastPosition && data.size() > 0) {
            recyclerView.smoothScrollToPosition(data.size() - 1);
            moveToLastPosition = false;
        }

        adapter = new NoteAdapter(this);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration
                (requireContext(), LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(Objects.requireNonNull
                (ContextCompat.getDrawable(getContext(), R.drawable.separator)));
        recyclerView.addItemDecoration(itemDecoration);

        adapter.setOnItemClickListener((position, note) -> {
            navigation.addFragment(NoteBlankFragment.newInstance(data.getNote(position)),
                    true);
            publisher.subscribe(note1 -> {
                data.updateNote(position, note1);
                adapter.notifyItemChanged(position);
            });
        });
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v,
                                    @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = adapter.getMenuPosition();
        if (item.getItemId() == R.id.menu_delete_note) {
            DeleteDialogFragment deleteDlgFragment = new DeleteDialogFragment();
            deleteDlgFragment.setCancelable(false);
            deleteDlgFragment.setOnDialogListener(new OnDeleteDialogListener() {
                @Override
                public void onDelete() {
                    data.deleteNote(position);
                    adapter.notifyItemRemoved(position);
                    deleteDlgFragment.dismiss();
                }

                @Override
                public void onCancelDelete() {
                    deleteDlgFragment.dismiss();
                }
            });
            deleteDlgFragment.show(requireActivity().getSupportFragmentManager(),
                    "DeleteFragmentTag");
            return true;
        }
        return super.onContextItemSelected(item);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuItem addNote = menu.findItem(R.id.menu_add_note);
        MenuItem search = menu.findItem(R.id.menu_search);
        search.setVisible(true);
        addNote.setOnMenuItemClickListener(item -> {
            navigation.addFragment(NoteBlankFragment.newInstance(), true);
            publisher.subscribe(note -> {
                data.addNote(note);
                adapter.notifyItemInserted(data.size() - 1);
                moveToLastPosition = true;
            });
            return true;
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

}