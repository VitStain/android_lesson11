package com.example.makingnotes.data;

public interface NoteSourceInt {
    int size();

    NoteSourceInt init(NoteSourceResponse noteSourceResponse);

    Notes getNote(int position);

    void deleteNote(int position);

    void updateNote(int position, Notes note);

    void addNote(Notes note);

}
