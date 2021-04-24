package com.example.makingnotes.data;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NoteSourceFirebaseImpl implements NoteSourceInt {

    private static final String NOTE_COLLECTION = "notes";
    private static final String TAG = "[NoteSourceFirebase]";

    // База данных Firestore
    private FirebaseFirestore store = FirebaseFirestore.getInstance();

    // Коллекция документов
    private CollectionReference collection = store.collection(NOTE_COLLECTION);

    // Загружаемый список карточек
    private List<Notes> notes = new ArrayList<>();

    @Override
    public NoteSourceInt init(final NoteSourceResponse noteSourceResponse) {
        // Получить всю коллекцию, отсортированную по полю «Дата»
        collection.orderBy(NoteDataMapping.Fields.DATE, Query.Direction.DESCENDING).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        notes = new ArrayList<>();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Map<String, Object> doc = document.getData();
                            String id = document.getId();
                            Notes note = NoteDataMapping.toNoteData(id, doc);
                            notes.add(note);
                        }
                        Log.d(TAG, "success " + notes.size() + " qnt");
                        noteSourceResponse.initialized(NoteSourceFirebaseImpl.this);
                    } else {
                        Log.d(TAG, "get failed with " + task.getException());
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "get failed with " + e));
        return this;
    }

    @Override
    public Notes getNote(int position) {
        return notes.get(position);
    }

    @Override
    public int size() {
        if (notes == null) {
            return 0;
        }
        return notes.size();
    }

    @Override
    public void deleteNote(int position) {
        // Удалить документ с определённым идентификатором
        collection.document(notes.get(position).getId()).delete();
        notes.remove(position);
    }

    @Override
    public void updateNote(int position, Notes note) {
        String id = note.getId();
        // Изменить документ по идентификатору
        collection.document(id).set(NoteDataMapping.toDocument(note));
        notes.set(position, note);
    }

    @Override
    public void addNote(final Notes note) {
        // Добавить документ
        collection.add(NoteDataMapping.toDocument(note)).addOnSuccessListener
                (documentReference -> note.setId(documentReference.getId()));
    }

}


