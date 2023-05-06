package com.example.mapsgt.data.dao;

import static android.content.ContentValues.TAG;

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

public class UserDAO extends RealtimeDatabase<User> {
    private final MutableLiveData<ArrayList<User>> usersLiveData = new MutableLiveData<>();
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();

    public UserDAO(DatabaseReference databaseReference) {
        super(databaseReference);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<User> userList = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    userList.add(user);
                }
                usersLiveData.setValue(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserDao", "Error reading users from database", error.toException());
            }
        };
        getDatabaseReference().addValueEventListener(valueEventListener);
    }
    @Override
    public void insert(User user) {
        getDatabaseReference().child(user.getId()).setValue(user);
    }

    @Override
    public void update(User user) {
        getDatabaseReference().child(user.getId()).setValue(user);
    }

    @Override
    public void delete(User user) {
        getDatabaseReference().child(user.getId()).removeValue();
    }

    @Override
    public void deleteAll() {
        getDatabaseReference().removeValue();
    }

    @Override
    public LiveData<ArrayList<User>> getAll() {
        return usersLiveData;
    }
    public LiveData<User> getUserById(String id) {
        MutableLiveData<User> currentUserLiveData = new MutableLiveData<>();
        Query query = getDatabaseReference().child(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                currentUserLiveData.setValue(user);
                Log.d("UserDao", "User found: " + user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserDao", "Error reading users from database", databaseError.toException());
            }
        });
        return currentUserLiveData;
    }
    public LiveData<User> getUserByEmail(String emailAddress) {
        Query query = getDatabaseReference().orderByChild("email").equalTo(emailAddress);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    userLiveData.setValue(user);
                    Log.d("UserDao", "User found: " + user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserDao", "Error reading users from database", databaseError.toException());
            }
        });
        return userLiveData;
    }

    public LiveData<User> getUserByPhone(String phoneNumber) {
        Query query = getDatabaseReference().orderByChild("phone").equalTo(phoneNumber);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    userLiveData.setValue(user);
                    Log.d("UserDao", "User found: " + user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserDao", "Error reading users from database", databaseError.toException());
            }
        });
        return userLiveData;
    }

    public LiveData<ArrayList<User>> getSuggestedUsers(double latitude, double longitude) {
        MutableLiveData<ArrayList<User>> suggestedUsersLiveData = new MutableLiveData<>();

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
                        ArrayList<User> suggestedUsers = suggestedUsersLiveData.getValue();
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
}
