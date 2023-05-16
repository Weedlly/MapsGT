package com.example.mapsgt.data.dao;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mapsgt.data.entities.User;
import com.example.mapsgt.database.RealtimeDatabase;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserDAO extends RealtimeDatabase<User> {

    public LiveData<User> getByEmail(String email) {
        MutableLiveData<User> liveData = new MutableLiveData<>();
        Query query = getDBRef().child(getFirebaseNode()).orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        liveData.setValue(user);
                    }
                } else {
                    liveData.setValue(null);
                }
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ", error.toException());
            }
        });
        return liveData;
    }

    public LiveData<User> getByPhone(String phone) {
        MutableLiveData<User> liveData = new MutableLiveData<>();
        Query query = getDBRef().child(getFirebaseNode()).orderByChild("phone").equalTo(phone);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        liveData.setValue(user);
                    }
                } else {
                    liveData.setValue(null);
                }
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ", error.toException());
            }
        });
        return liveData;
    }

    public void setIsSharing(String id, Boolean isSharing) {
        getDBRef().child(getFirebaseNode()).child(id).child("isSharing").setValue(isSharing);
    }

    public LiveData<List<User>> getSuggestedUsers(double latitude, double longitude) {
        MutableLiveData<List<User>> suggestedUsersLiveData = new MutableLiveData<>();

        GeoFire geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference("geofire"));
        GeoLocation center = new GeoLocation(latitude, longitude);
        double radiusInKm = 5.0;
        GeoQuery geoQuery = geoFire.queryAtLocation(center, radiusInKm);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                FirebaseDatabase.getInstance().getReference("users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Lấy thông tin của người dùng từ snapshot và hiển thị trong danh sách gợi ý
                        User user = snapshot.getValue(User.class);
                        List<User> suggestedUsers = suggestedUsersLiveData.getValue();
                        if (suggestedUsers == null) {
                            suggestedUsers = new ArrayList<>();
                        }
                        suggestedUsers.add(user);
                        suggestedUsersLiveData.setValue(suggestedUsers);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý lỗi khi lấy dữ liệu từ Firebase Realtime Database
                    }
                });
            }

            @Override
            public void onKeyExited(String key) {
                // Xử lý khi địa điểm trong bán kính bị xoá
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                // Xử lý khi địa điểm trong bán kính di chuyển
            }

            @Override
            public void onGeoQueryReady() {
                // Khi đã lấy hết các địa điểm trong bán kính, thông báo kết thúc để cập nhật giao diện;
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });

        return suggestedUsersLiveData;
    }

    public void setLocation(String id, double latitude, double longitude) {
        DatabaseReference geoFireRef = FirebaseDatabase.getInstance().getReference().child("geofire");
        GeoFire geoFire = new GeoFire(geoFireRef);
        geoFire.setLocation(id, new GeoLocation(latitude, longitude));
    }

    @Override
    public String getFirebaseNode() {
        return "users";
    }

    @Override
    protected Class<User> getGenericType() {
        return User.class;
    }
}
