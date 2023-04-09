package com.example.mapsgt.ui.add_friend.find_friend;

import com.example.mapsgt.data.entities.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FindFriendViewModel {
    private FirebaseDatabase ListUserData;
    private DatabaseReference allUserFromDatabaseRef;

    public FindFriendViewModel() {
        ListUserData = FirebaseDatabase.getInstance();
        allUserFromDatabaseRef = ListUserData.getReference("User");
    }

    public ArrayList<User> findFriend(String query) {
        ArrayList<User> users = new ArrayList<>();

        return users;
    }
}
