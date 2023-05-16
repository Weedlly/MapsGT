package com.example.mapsgt.data.dao;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mapsgt.database.RealtimeDatabase;
import com.example.mapsgt.ui.map.FavouritePlace;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavouritePlaceDAO extends RealtimeDatabase<FavouritePlace> {

    public LiveData<List<FavouritePlace>> getFavoritePlaceList(String userId) {
        MutableLiveData<List<FavouritePlace>> favoritePlaces = new MutableLiveData<>();
        Query query = getDBRef().child(getFirebaseNode()).orderByChild("userId").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<FavouritePlace> favoritePlacesRes = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        FavouritePlace place = dataSnapshot.getValue(FavouritePlace.class);
                        place.setId(dataSnapshot.getKey());
                        favoritePlacesRes.add(place);
                    }
                    favoritePlaces.setValue(favoritePlacesRes);
                } else {
                    favoritePlaces.setValue(null);
                }
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ", error.toException());
            }
        });
        return favoritePlaces;
    }

    @Override
    public String getFirebaseNode() {
        return "favourite_places";
    }

    @Override
    protected Class<FavouritePlace> getGenericType() {
        return FavouritePlace.class;
    }
}
