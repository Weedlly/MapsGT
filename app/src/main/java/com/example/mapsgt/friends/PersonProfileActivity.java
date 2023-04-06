package com.example.mapsgt.friends;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mapsgt.R;
import com.example.mapsgt.data.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {

    private TextView userName, userProfileName, userStatus, userCountry, userGender, userDOB;
    private CircleImageView userProfileImage;
    private Button SendFriendRequestBTN, DeclineFriendRequestBTN;

    private DatabaseReference FriendRequestFef, UsersRef, FriendsFef;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    private String senderUserId, receiverUserId, CURRENT_STATE, saveCurrentDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        senderUserId = "User_0";  //mAuth.getCurrentUser().getUid(); (Demo)
        Intent intent = getIntent();
        receiverUserId = "User_" +intent.getStringExtra("visit_user_id");
        Log.d(TAG, "Id visit: " + receiverUserId);

        UsersRef = FirebaseDatabase.getInstance().getReference().child("User");
        FriendRequestFef = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
        FriendsFef = FirebaseDatabase.getInstance().getReference().child("Friends");

        InitializeFields();

        UsersRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {

                    // String personProfileImage = snapshot.child("profileimage").getValue().toString(); //Todo
                    String personUserName = snapshot.child("email").getValue().toString();
                    String personProfileName = snapshot.child("firstName").getValue().toString() + " " + snapshot.child("lastName").getValue().toString();
                    String personProfileStatus = "Location: " + snapshot.child("lastKnownLocationId").getValue().toString();
                    // String personDOB = "DOB: " + snapshot.child("dateOfBirth").getValue().toString(); //Todo
                    String personCountry = "Phone: " + snapshot.child("phone").getValue().toString();
                    String personGender = snapshot.child("gender").getValue().toString();

                    //Picasso.with(PersonProfileActivity.this).load(personProfileImage).placeholder(R.drawable.ic_profile);

                    userName.setText( personUserName);
                    userProfileName.setText(personProfileName);
                    userStatus.setText(personProfileStatus);
                    //userDOB.setText(personDOB);
                    userCountry.setText(personCountry);
                    userGender.setText(personGender);

                    MaintananceofButton();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DeclineFriendRequestBTN.setVisibility(View.INVISIBLE);
        DeclineFriendRequestBTN.setEnabled(false);

        if (!senderUserId.equals(receiverUserId))
        {
            SendFriendRequestBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SendFriendRequestBTN.setEnabled(false);
                    
                    if (CURRENT_STATE.equals("not_friends"))
                    {
                        SendFriendRequestToPerson();
                    }
                    if (CURRENT_STATE.equals("request_sent"))
                    {
                        CancelFriendRequest();
                    }
                    if (CURRENT_STATE.equals("request_received"))
                    {
                        AcceptFriendRequest();
                    }
                    if (CURRENT_STATE.equals("friends"))
                    {
                        UnFriendAnExistingFriend();
                    }
                }
            });
        }
        else {
            DeclineFriendRequestBTN.setVisibility(View.INVISIBLE);
            SendFriendRequestBTN.setEnabled(false);
        }
    }

    private void UnFriendAnExistingFriend() {
        FriendsFef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            FriendsFef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                SendFriendRequestBTN.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                SendFriendRequestBTN.setText("Send Friend Request");

                                                DeclineFriendRequestBTN.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestBTN.setEnabled(false);
                                            }
                                        }
                                    });

                        }
                    }
                });
    }

    private void AcceptFriendRequest() {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        FriendsFef.child(senderUserId).child(receiverUserId).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            FriendsFef.child(receiverUserId).child(senderUserId).child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                FriendRequestFef.child(senderUserId).child(receiverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful())
                                                                {
                                                                    FriendRequestFef.child(receiverUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful())
                                                                                    {
                                                                                        SendFriendRequestBTN.setEnabled(true);
                                                                                        CURRENT_STATE = "friends";
                                                                                        SendFriendRequestBTN.setText("UnFriend this Person");

                                                                                        DeclineFriendRequestBTN.setVisibility(View.INVISIBLE);
                                                                                        DeclineFriendRequestBTN.setEnabled(false);
                                                                                    }
                                                                                }
                                                                            });

                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
    private void CancelFriendRequest() {
        FriendRequestFef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            FriendRequestFef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                SendFriendRequestBTN.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                SendFriendRequestBTN.setText("Send Friend Request");

                                                DeclineFriendRequestBTN.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestBTN.setEnabled(false);
                                            }
                                        }
                                    });

                        }
                    }
                });
    }

    private void MaintananceofButton() {
        FriendRequestFef.child(senderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(receiverUserId))
                        {
                            String request_type = snapshot.child(receiverUserId).child("request_type").getValue().toString();

                            if (request_type.equals("sent"))
                            {
                                CURRENT_STATE = "request_sent";
                                SendFriendRequestBTN.setText("Cancel friend request");

                                DeclineFriendRequestBTN.setVisibility(View.INVISIBLE);
                                DeclineFriendRequestBTN.setEnabled(false);
                            }
                            else if (request_type.equals("received"))
                            {
                                CURRENT_STATE = "request_received";
                                SendFriendRequestBTN.setText("Accept friend request");

                                DeclineFriendRequestBTN.setVisibility(View.VISIBLE);
                                DeclineFriendRequestBTN.setEnabled(true);

                                DeclineFriendRequestBTN.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        CancelFriendRequest();
                                    }
                                });
                            }
                        }
                        else
                        {
                            FriendsFef.child(senderUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.hasChild(receiverUserId))
                                            {
                                                CURRENT_STATE = "friends";
                                                SendFriendRequestBTN.setText("Unfriend this person");

                                                DeclineFriendRequestBTN.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestBTN.setEnabled(false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void SendFriendRequestToPerson() {
        //Log.d(TAG, "Debug sent friend request");
        FriendRequestFef.child(senderUserId).child(receiverUserId)
            .child("request_type").setValue("sent")
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "Debug sent friend request");
                    if (task.isSuccessful())
                    {
                        FriendRequestFef.child(receiverUserId).child(senderUserId)
                            .child("request_type").setValue("received")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        SendFriendRequestBTN.setEnabled(true);
                                        CURRENT_STATE = "request_sent";
                                        SendFriendRequestBTN.setText("Unfriend");

                                        DeclineFriendRequestBTN.setVisibility(View.INVISIBLE);
                                        DeclineFriendRequestBTN.setEnabled(false);
                                    }
                                }
                            });

                    }
                }
            });
    }

    private void InitializeFields()
    {
        userName = (TextView) findViewById(R.id.person_username);
        userProfileName = (TextView) findViewById(R.id.person_profile_full_name);
        userStatus = (TextView) findViewById(R.id.person_profile_status);
        userCountry = (TextView) findViewById(R.id.person_country);
        userGender = (TextView) findViewById(R.id.person_gender);
        userDOB = (TextView) findViewById(R.id.person_dob);
        userProfileImage = (CircleImageView) findViewById(R.id.person_profile_pic);
        SendFriendRequestBTN = (Button) findViewById(R.id.person_send_friend_request_btn);
        DeclineFriendRequestBTN = (Button) findViewById(R.id.person_decline_friend_request_btn);

        CURRENT_STATE = "not_friends";
    }


}