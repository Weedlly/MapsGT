package com.example.mapsgt.data.dao;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mapsgt.data.entities.Friend;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendRelationshipDAO {
    private DatabaseReference databaseRef;

    public FriendRelationshipDAO() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("FriendRelationship");
    }

    public LiveData<List<Friend>> getFriendList(String userId) {
        MutableLiveData<List<Friend>> friendList = new MutableLiveData<>();
        databaseRef.child(userId).child("Friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Friend> userList = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Friend user = userSnapshot.getValue(Friend.class);
                    userList.add(user);
                }
                friendList.postValue(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FriendRelationshipDAO", "Error reading users from database", databaseError.toException());
            }
        });
        return friendList;
    }
}
