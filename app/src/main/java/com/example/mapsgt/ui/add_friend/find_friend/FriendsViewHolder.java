package com.example.mapsgt.ui.add_friend.find_friend;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mapsgt.data.dao.FriendDAO;
import com.example.mapsgt.data.dao.UserDAO;
import com.example.mapsgt.data.entities.Friend;
import com.example.mapsgt.data.entities.User;
import com.google.firebase.database.FirebaseDatabase;

public class FriendsViewHolder extends ViewModel {
    private final FriendDAO friendDAO;

    public FriendsViewHolder() {
        friendDAO = new FriendDAO(FirebaseDatabase.getInstance().getReference("users").child("O21R3tAHqjXH7uKEgUMlteCH8r03").child("Friends"));
    }

}


