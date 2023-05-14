package com.example.mapsgt.database;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public abstract class NewRealtimeDatabase<T> {
    private DatabaseReference mDatabaseReference;

    public NewRealtimeDatabase() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public abstract String getFirebaseNode();

    public void insert(T item) {
        DatabaseReference newItemRef = mDatabaseReference.child(getFirebaseNode()).push();
        newItemRef.setValue(item);
    }

    public void update(String key, T item) {
        DatabaseReference itemRef = mDatabaseReference.child(getFirebaseNode()).child(key);
        itemRef.setValue(item);
    }

    public void delete(String key) {
        DatabaseReference itemRef = mDatabaseReference.child(getFirebaseNode()).child(key);
        itemRef.removeValue();
    }

    public LiveData<List<T>> getAll() {
        MutableLiveData<List<T>> liveData = new MutableLiveData<>();
        DatabaseReference itemsRef = mDatabaseReference.child(getFirebaseNode());
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<T> itemList = new ArrayList<>();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    T item = itemSnapshot.getValue(getGenericType());
                    itemList.add(item);
                }
                liveData.setValue(itemList);
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ", error.toException());
            }
        });
        return liveData;
    }

    public LiveData<T> getByKey(String key) {
        MutableLiveData<T> liveData = new MutableLiveData<>();
        DatabaseReference itemRef = mDatabaseReference.child(getFirebaseNode()).child(key);
        itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    T item = snapshot.getValue(getGenericType());
                    liveData.setValue(item);
                } else {
                    liveData.setValue(null);
                }
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ", error.toException());
            }
        });
        return liveData;
    }

    protected abstract Class<T> getGenericType();

    public DatabaseReference getDBRef() {
        return mDatabaseReference;
    }
}
