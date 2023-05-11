package com.example.mapsgt.utils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountFirebaseUtil {
    public static void checkEmailExists(String email, OnEmailExistsCallback callback) {
        FirebaseDatabase database= FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");
        usersRef.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            callback.onEmailExists(true);
                        } else {
                            callback.onEmailExists(false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onEmailExists(null);
                    }
                });
    }

    public interface OnEmailExistsCallback {
        void onEmailExists(Boolean exists);
    }
}
