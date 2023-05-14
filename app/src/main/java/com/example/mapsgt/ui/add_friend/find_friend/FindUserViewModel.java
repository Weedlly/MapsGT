package com.example.mapsgt.ui.add_friend.find_friend;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mapsgt.data.dao.UserDAO;
import com.example.mapsgt.data.entities.User;

public class FindUserViewModel extends ViewModel {
    private final UserDAO userDAO;

    public FindUserViewModel() {
        userDAO = new UserDAO();
    }

    public LiveData<User> findFriendByEmail(String email) {
        return userDAO.getByEmail(email);
    }

    public LiveData<User> findFriendByPhone(String id) {
        return userDAO.getByPhone(id);
    }


}
