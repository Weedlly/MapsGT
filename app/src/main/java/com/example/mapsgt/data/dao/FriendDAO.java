package com.example.mapsgt.data.dao;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mapsgt.data.entities.Friend;
import com.example.mapsgt.database.RealtimeDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendDAO extends RealtimeDatabase<Friend> {
    private final MutableLiveData<ArrayList<Friend>> friendsLiveData = new MutableLiveData<>();

    public FriendDAO(DatabaseReference databaseReference) {
        super(databaseReference);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Friend> userList = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Friend user = userSnapshot.getValue(Friend.class);
                    userList.add(user);
                }
                friendsLiveData.setValue(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FriendDao", "Error reading users from database", error.toException());
            }
        };
        getDatabaseReference().addValueEventListener(valueEventListener);
    }

    @Override
    public void insert(Friend friend) {
        getDatabaseReference().child(friend.getID()).setValue(friend);
    }

    @Override
    public void update(Friend friend) {
        getDatabaseReference().child(friend.getID()).setValue(friend);
    }

    @Override
    public void delete(Friend friend) {
        getDatabaseReference().child(friend.getID()).removeValue();
    }

    @Override
    public void deleteAll() {
        getDatabaseReference().removeValue();
    }

    @Override
    public LiveData<ArrayList<Friend>> getAll() {
        return friendsLiveData;
    }
}
