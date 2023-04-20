package com.example.mapsgt.database;

import androidx.lifecycle.LiveData;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public abstract class RealtimeDatabase<T> {
    private final DatabaseReference databaseReference;

    public RealtimeDatabase(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public abstract void insert(T object);

    public abstract void update(T object);

    public abstract void delete(T object);

    public abstract void deleteAll();

    public abstract LiveData<ArrayList<T>> getAll();
}
