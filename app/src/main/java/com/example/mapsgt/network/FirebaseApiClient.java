package com.example.mapsgt.network;

import static com.example.mapsgt.constant.ApiConstant.FIREBASE_BASE_URL;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FirebaseApiClient {
    private static FirebaseApi firebaseApi;

    public static FirebaseApi getFirebaseApi() {
        if(firebaseApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(FIREBASE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            firebaseApi = retrofit.create(FirebaseApi.class);
        }
        return firebaseApi;
    }
}
