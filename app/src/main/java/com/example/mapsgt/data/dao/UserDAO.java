package com.example.mapsgt.data.dao;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mapsgt.data.entities.User;
import com.example.mapsgt.database.RealtimeDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserDAO extends RealtimeDatabase<User> {
    private final MutableLiveData<ArrayList<User>> usersLiveData = new MutableLiveData<>();
    public UserDAO(DatabaseReference databaseReference) {
        super(databaseReference);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<User> userList = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    userList.add(user);
                }
                usersLiveData.setValue(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserDao", "Error reading users from database", error.toException());
            }
        };
        getDatabaseReference().addValueEventListener(valueEventListener);
    }
    @Override
    public void insert(User user) {
        getDatabaseReference().child(user.getId()).setValue(user);
    }

    @Override
    public void update(User user) {
        getDatabaseReference().child(user.getId()).setValue(user);
    }

    @Override
    public void delete(User user) {
        getDatabaseReference().child(user.getId()).removeValue();
    }

    @Override
    public void deleteAll() {
        getDatabaseReference().removeValue();
    }

    @Override
    public LiveData<ArrayList<User>> getAll() {
        return usersLiveData;
    }

    public User getUserByEmail(String emailAddress) {
        if (usersLiveData.getValue() == null) {
            return null;
        }
        for (User user : usersLiveData.getValue()) {
            if (user.getEmail().equals(emailAddress)) {
                return user;
            }
        }
        return null;
    }
}
