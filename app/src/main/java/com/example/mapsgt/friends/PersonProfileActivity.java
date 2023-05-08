package com.example.mapsgt.friends;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import android.bluetooth.BluetoothClass;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mapsgt.R;
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
    private Button SendFriendRequestBTN, DeclineFriendRequestBTN, BlockFriendBTN;

    private DatabaseReference FriendRequestFef, UsersRef, FriendsFef, senderFriendRef ,receiverFriendRef;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String senderUserId , CURRENT_STATE, saveCurrentDate;
    private static String receiverUserId ;
    private boolean isBlocked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        //TODO: change mAuth.getCurrentUser().getUid(); by below ID to testing
        senderUserId = mAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        receiverUserId = intent.getStringExtra("visit_user_id");

        UsersRef = FirebaseDatabase.getInstance().getReference().child("users");
        FriendsFef = FirebaseDatabase.getInstance().getReference().child("FriendRelationship");
        FriendRequestFef = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
        senderFriendRef = FriendsFef.child(senderUserId).child("Friends");
        receiverFriendRef = FriendsFef.child(receiverUserId).child("Friends");
        InitializeFields();

        UsersRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String personProfileImage = snapshot.child("profilePicture").getValue().toString();
                    String personUserName = snapshot.child("email").getValue().toString();
                    String personProfileName = snapshot.child("firstName").getValue().toString() + " " + snapshot.child("lastName").getValue().toString();
                    String personProfileStatus = "Location: (" + snapshot.child("longitude").getValue().toString() + ", " + snapshot.child("latitude").getValue().toString() + ")";
                    String personDOB = "DOB: " + snapshot.child("dateOfBirth").getValue().toString();
                    String personCountry = "Phone: " + snapshot.child("phone").getValue().toString();
                    String personGender = "General: " + snapshot.child("gender").getValue().toString();

                    Picasso.with(PersonProfileActivity.this).load(personProfileImage).placeholder(R.drawable.ic_profile);

                    Glide.with(PersonProfileActivity.this)
                            .load(personProfileImage)
                            .placeholder(R.drawable.ic_profile) // optional placeholder image
                            .error(R.drawable.google) // optional error image
                            .into(userProfileImage);

                    userName.setText( personUserName);
                    userProfileName.setText(personProfileName);
                    userStatus.setText(personProfileStatus);
                    userDOB.setText(personDOB);
                    userCountry.setText(personCountry);
                    userGender.setText(personGender);

                    maintananceOfButton();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DeclineFriendRequestBTN.setVisibility(View.INVISIBLE);
        DeclineFriendRequestBTN.setEnabled(false);
        BlockFriendBTN.setVisibility(View.INVISIBLE);
        BlockFriendBTN.setEnabled(false);

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
            SendFriendRequestBTN.setVisibility(View.INVISIBLE);
            BlockFriendBTN.setVisibility(View.INVISIBLE);
        }
    }

    private void unblockFriend() {
        senderFriendRef.child(receiverUserId)
                .child("status")
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            receiverFriendRef.child(senderUserId)
                                    .child("status")
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                backToFriendState();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void blockFriend() {
        String block = "Blocked";
        String beBlock = "beBlocked";
        senderFriendRef.child(receiverUserId).child("status").setValue(block)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        receiverFriendRef.child(senderUserId).child("status").setValue(beBlock)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            SendFriendRequestBTN.setVisibility(View.INVISIBLE);
                                            SendFriendRequestBTN.setEnabled(false);
                                            DeclineFriendRequestBTN.setVisibility(View.INVISIBLE);
                                            DeclineFriendRequestBTN.setEnabled(false);

                                            BlockFriendBTN.setEnabled(true);
                                            BlockFriendBTN.setText("UnBlocked");
                                        }
                                    }
                                });
                    }
                });
    }
    private void UnFriendAnExistingFriend() {
        senderFriendRef.child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            receiverFriendRef.child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                backToFriendState();

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
        addIdValueToEachUserFriend();
        senderFriendRef.child(receiverUserId).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            receiverFriendRef.child(senderUserId).child("date").setValue(saveCurrentDate)
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
                                                                                        backToFriendState();
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

    private void maintananceOfButton() {
        FriendRequestFef.child(senderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(receiverUserId)) {
                            String request_type = snapshot.child(receiverUserId).child("request_type").getValue().toString();

                            if (request_type.equals("sent")) {
                                CURRENT_STATE = "request_sent";
                                SendFriendRequestBTN.setText("Cancel friend request");

                                DeclineFriendRequestBTN.setVisibility(View.INVISIBLE);
                                DeclineFriendRequestBTN.setEnabled(false);

                                BlockFriendBTN.setVisibility(View.INVISIBLE);
                                BlockFriendBTN.setEnabled(false);

                            } else if (request_type.equals("received")) {
                                CURRENT_STATE = "request_received";
                                SendFriendRequestBTN.setText("Accept friend request");

                                DeclineFriendRequestBTN.setVisibility(View.VISIBLE);
                                DeclineFriendRequestBTN.setEnabled(true);

                                BlockFriendBTN.setVisibility(View.INVISIBLE);
                                BlockFriendBTN.setEnabled(false);

                                DeclineFriendRequestBTN.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) { CancelFriendRequest(); }
                                });
                            }
                        } else {
                            senderFriendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(receiverUserId)) {
                                        validBeBlockedFriend();


                                        if (!isBlocked) {
                                            backToFriendState();
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

    }

    private void validBeBlockedFriend() //For sender block
    {
        senderFriendRef.child(receiverUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("status")) {
                    String status = snapshot.child("status").getValue().toString();
                    if (status.equals("Blocked")) {
                        CURRENT_STATE = "blocked";
                        SendFriendRequestBTN.setVisibility(View.INVISIBLE);
                        SendFriendRequestBTN.setEnabled(false);

                        DeclineFriendRequestBTN.setVisibility(View.INVISIBLE);
                        DeclineFriendRequestBTN.setEnabled(false);

                        BlockFriendBTN.setVisibility(View.VISIBLE);
                        BlockFriendBTN.setEnabled(true);
                        BlockFriendBTN.setText("UnBlocked");
                        isBlocked = true;

                        BlockFriendBTN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                isBlocked = false;
                                unblockFriend();

                            }
                        });
                    }
                    if (status.equals("beBlocked")) {
                        CURRENT_STATE = "blocked";
                        SendFriendRequestBTN.setVisibility(View.INVISIBLE);
                        SendFriendRequestBTN.setEnabled(false);

                        DeclineFriendRequestBTN.setVisibility(View.INVISIBLE);
                        DeclineFriendRequestBTN.setEnabled(false);

                        BlockFriendBTN.setVisibility(View.VISIBLE);
                        BlockFriendBTN.setEnabled(false);
                        BlockFriendBTN.setText("You are be blocked");
                        isBlocked = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SendFriendRequestToPerson() {
        FriendRequestFef.child(senderUserId).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
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
                                                SendFriendRequestBTN.setText("Cancel Sent Request");

                                                DeclineFriendRequestBTN.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestBTN.setEnabled(false);
                                            }
                                        }
                                    });

                        }
                    }
                });
    }

    private void backToFriendState()
    {
        SendFriendRequestBTN.setEnabled(true);
        SendFriendRequestBTN.setVisibility(View.VISIBLE);
        CURRENT_STATE = "friends";
        SendFriendRequestBTN.setText("Unfriend this person");

        DeclineFriendRequestBTN.setVisibility(View.INVISIBLE);
        DeclineFriendRequestBTN.setEnabled(false);

        //TODO
        BlockFriendBTN.setVisibility(View.VISIBLE);
        BlockFriendBTN.setEnabled(true);
        BlockFriendBTN.setText("Blocked");
        BlockFriendBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BlockFriendBTN.setEnabled(false);
                CURRENT_STATE = "blocked";
                blockFriend();
            }
        });
    }

    private void addIdValueToEachUserFriend() {
        senderFriendRef.child(receiverUserId).child("id").setValue(receiverUserId);
        receiverFriendRef.child(senderUserId).child("id").setValue(senderUserId);
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
        BlockFriendBTN = (Button) findViewById(R.id.person_block_friend_btn);
        CURRENT_STATE = "not_friends";
    }


}