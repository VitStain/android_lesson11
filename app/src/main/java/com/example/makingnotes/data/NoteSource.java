package com.example.makingnotes.data;


import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.makingnotes.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class NoteSource implements Parcelable {
    public static final Creator<NoteSource> CREATOR = new Creator<NoteSource>() {
        @Override
        public NoteSource createFromParcel(Parcel in) {
            return new NoteSource(in);
        }

        @Override
        public NoteSource[] newArray(int size) {
            return new NoteSource[size];
        }
    };
    private ArrayList<Notes> notes;
    private Resources resources;     // ресурсы приложения
    private int counter = 0;

    public NoteSource(Resources resources) {
        this.resources = resources;
        notes = new ArrayList<>();
    }

    protected NoteSource(Parcel in) {
        notes = in.createTypedArrayList(Notes.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(notes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public NoteSourceInt init(NoteSourceResponse noteSourceResponse) {
        Notes[] notesArray = new Notes[]{
                new Notes(
                        resources.getString(R.string.one_note_title),
                        resources.getString(R.string.one_note_content),
                        getDateOfCreation()),
                new Notes(resources.getString(R.string.two_note_title),
                        resources.getString(R.string.two_note_content),
                        getDateOfCreation()),
                new Notes(resources.getString(R.string.three_note_title),
                        resources.getString(R.string.three_note_content),
                        getDateOfCreation()),
                new Notes(resources.getString(R.string.four_note_title),
                        resources.getString(R.string.four_note_content),
                        getDateOfCreation()),
                new Notes(resources.getString(R.string.five_note_title),
                        resources.getString(R.string.five_note_content),
                        getDateOfCreation()),

        };
        Collections.addAll(notes, notesArray);
        if (noteSourceResponse != null) {
            noteSourceResponse.initialized((NoteSourceInt) this);
        }
        return (NoteSourceInt) this;
    }

    public Notes getNote(int position) {
        return notes.get(position);
    }

    public int size() {
        return notes.size();
    }

    public void deleteNote(int position) {
        notes.remove(position);
    }

    public void changeNote(int position, Notes note) {
        notes.set(position, note);
    }

    public void addNote(Notes note) {
        notes.add(note);
    }

    public String getDateOfCreation() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy",
                Locale.getDefault());
        return String.format("%s: %s", "Дата создания",
                formatter.format(Calendar.getInstance().getTime()));
    }

    public int getColor() {
        int[] colors = resources.getIntArray(R.array.colors);
        int color = colors[counter];
        if (counter < colors.length - 1) {
            counter++;
        } else counter = 0;
        return color;
    }
}