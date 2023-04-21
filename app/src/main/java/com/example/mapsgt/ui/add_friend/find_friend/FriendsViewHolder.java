package com.example.mapsgt.ui.add_friend.find_friend;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mapsgt.data.dao.UserDAO;
import com.example.mapsgt.data.entities.User;
import com.google.firebase.database.FirebaseDatabase;

public class FriendsViewHolder extends ViewModel {
    private final UserDAO userDAO;

    public FriendsViewHolder() {
        userDAO = new UserDAO(FirebaseDatabase.getInstance().getReference("users").child("Friends"));
    }

    public LiveData<User> getUserById(String id) { return userDAO.getUserById(id); }
}


