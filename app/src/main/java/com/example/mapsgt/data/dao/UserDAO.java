package com.example.mapsgt.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.mapsgt.data.entities.Friendship;
import com.example.mapsgt.data.entities.User;

import java.util.List;

@Dao
public interface UserDAO {

    @Insert
    void insertUser(User user);

    @Insert
    void insertFriendship(Friendship friendship);

    @Query("SELECT * FROM user WHERE user_id = :userId")
    User getUserById(String userId);

    @Query("SELECT * FROM user WHERE email = :email")
    User getUserByEmail(String email);

    @Query("SELECT * FROM user")
    List<User> getUserList();

    @Transaction
    @Query("SELECT * FROM user WHERE user_id IN (SELECT friend_id FROM friendship WHERE user_id = :userId AND status = 'ACCEPTED')")
    List<User> getFriendsForUser(int userId);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);
}
