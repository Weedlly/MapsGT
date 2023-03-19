package com.example.mapsgt.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.mapsgt.data.entities.Comment;

@Dao
public interface CommentDAO {
    @Insert
    void insertComment(Comment comment);
}
