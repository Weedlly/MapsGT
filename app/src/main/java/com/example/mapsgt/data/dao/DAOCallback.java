package com.example.mapsgt.data.dao;

public interface DAOCallback<T> {
    void onSuccess(T object);
    void onFailure(Exception e);
}
