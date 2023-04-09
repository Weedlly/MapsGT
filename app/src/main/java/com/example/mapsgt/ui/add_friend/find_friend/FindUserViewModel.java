package com.example.mapsgt.ui.add_friend.find_friend;

import com.example.mapsgt.data.dao.UserDAO;
import com.example.mapsgt.data.entities.User;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FindUserViewModel {
    private final UserDAO userDAO;
    public FindUserViewModel() {
        userDAO = new UserDAO(FirebaseDatabase.getInstance().getReference("users"));
    }
    public ArrayList<User> findFriend(String query) {
        ArrayList<User> users = new ArrayList<>();
        userDAO.getAll().observeForever(users1 -> {
                    for (User user : users1) {
                        if (user.getEmail().contains(query)) {
                            users.add(user);
                        }
                    }
                }
        );
        return users;
    }
}
