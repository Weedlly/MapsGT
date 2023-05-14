package com.example.mapsgt.data.dao;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mapsgt.data.entities.Friend;
import com.example.mapsgt.database.RealtimeDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendRelationshipDAO extends RealtimeDatabase<Friend> {

    public LiveData<List<Friend>> getFriendList(String userId) {
        MutableLiveData<List<Friend>> friendList = new MutableLiveData<>();
        getDBRef().child(getFirebaseNode()).child(userId).child("Friends").addListenerForSingleValueEvent(new ValueEventListener() {
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

    @Override
    public String getFirebaseNode() {
        return "FriendRelationship";
    }

    @Override
    protected Class<Friend> getGenericType() {
        return null;
    }
}
