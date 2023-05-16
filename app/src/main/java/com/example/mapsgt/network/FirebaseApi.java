package com.example.mapsgt.network;

import com.example.mapsgt.data.entities.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface FirebaseApi {
    @GET("users/{userId}.json")
    Call<User> getUserById(@Path("userId") String userId);

    @PUT("users/{userId}.json")
    Call<User> createUser(@Path("userId") String userId, @Body User user);
}
