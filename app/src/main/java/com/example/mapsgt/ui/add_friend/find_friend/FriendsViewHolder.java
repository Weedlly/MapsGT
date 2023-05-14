package com.example.mapsgt.ui.add_friend.find_friend;


import androidx.lifecycle.ViewModel;

import com.example.mapsgt.data.dao.FriendDAO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class FriendsViewHolder extends ViewModel {
    private final FriendDAO friendDAO;
    private FirebaseAuth mAuth;

    public FriendsViewHolder() {
        friendDAO = new FriendDAO(FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid()).child("Friends"));
    }

}


