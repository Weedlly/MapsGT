package com.example.mapsgt.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.mapsgt.entities.User;
import com.example.mapsgt.entities.relations.UserWithFriendships;

import java.util.List;

@Dao
public interface UserDAO {

    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM user")
    List<User> getUserList();

    @Transaction
    @Query("SELECT * FROM user WHERE userId = :userId")
    List<UserWithFriendships> getUserWithFriendships(int userId);
}
