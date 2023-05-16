package com.example.mapsgt.data.dao;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mapsgt.database.RealtimeDatabase;
import com.example.mapsgt.data.entities.HistoryPlace;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryPlaceDAO extends RealtimeDatabase<HistoryPlace> {

    public LiveData<List<HistoryPlace>> getHistoryPlaceList(String userId) {
        MutableLiveData<List<HistoryPlace>> favoritePlaces = new MutableLiveData<>();
        Query query = getDBRef().child(getFirebaseNode()).orderByChild("userId").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<HistoryPlace> historyPlacesRes = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        HistoryPlace place = dataSnapshot.getValue(getGenericType());
                        place.setId(dataSnapshot.getKey());
                        historyPlacesRes.add(place);
                    }
                    favoritePlaces.setValue(historyPlacesRes);
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
        return "history_places";
    }

    @Override
    protected Class<HistoryPlace> getGenericType() {
        return HistoryPlace.class;
    }
}
