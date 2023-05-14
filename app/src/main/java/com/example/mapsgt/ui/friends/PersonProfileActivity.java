package com.example.mapsgt.ui.friends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {

    private TextView userName, userProfileName, userStatus, userCountry, userGender, userDOB;
    private CircleImageView userProfileImage;
    private Button sendFriendRequestBtn, declineFriendRequestBtn, blockFriendBtn;

    private DatabaseReference friendRequestRef, usersRef, friendsRef, senderFriendRef, receiverFriendRef;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String senderUserId, currentState, saveCurrentDate;
    private static String receiverUserId;
    private boolean isBlocked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        senderUserId = mAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        receiverUserId = intent.getStringExtra("visit_user_id");

        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        friendsRef = FirebaseDatabase.getInstance().getReference().child("FriendRelationship");
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
        senderFriendRef = friendsRef.child(senderUserId).child("Friends");
        receiverFriendRef = friendsRef.child(receiverUserId).child("Friends");
        initializeFields();

        usersRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
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

                    userName.setText(personUserName);
                    userProfileName.setText(personProfileName);
                    userStatus.setText(personProfileStatus);
                    userDOB.setText(personDOB);
                    userCountry.setText(personCountry);
                    userGender.setText(personGender);

                    maintenanceOfButton();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        declineFriendRequestBtn.setVisibility(View.INVISIBLE);
        declineFriendRequestBtn.setEnabled(false);
        blockFriendBtn.setVisibility(View.INVISIBLE);
        blockFriendBtn.setEnabled(false);

        if (!senderUserId.equals(receiverUserId)) {
            sendFriendRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendFriendRequestBtn.setEnabled(false);

                    if (currentState.equals("not_friends")) {
                        sendFriendRequestToPerson();
                    }
                    if (currentState.equals("request_sent")) {
                        cancelFriendRequest();
                    }
                    if (currentState.equals("request_received")) {
                        acceptFriendRequest();
                    }
                    if (currentState.equals("friends")) {
                        unFriendAnExistingFriend();
                    }
                }
            });
        } else {
            declineFriendRequestBtn.setVisibility(View.INVISIBLE);
            sendFriendRequestBtn.setVisibility(View.INVISIBLE);
            blockFriendBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void unblockFriend() {
        senderFriendRef.child(receiverUserId)
                .child("status")
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            receiverFriendRef.child(senderUserId)
                                    .child("status")
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
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
                                        if (task.isSuccessful()) {
                                            sendFriendRequestBtn.setVisibility(View.INVISIBLE);
                                            sendFriendRequestBtn.setEnabled(false);
                                            declineFriendRequestBtn.setVisibility(View.INVISIBLE);
                                            declineFriendRequestBtn.setEnabled(false);

                                            blockFriendBtn.setEnabled(true);
                                            blockFriendBtn.setText("UnBlocked");
                                        }
                                    }
                                });
                    }
                });
    }

    private void unFriendAnExistingFriend() {
        senderFriendRef.child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            receiverFriendRef.child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                backToFriendState();

                                            }
                                        }
                                    });

                        }
                    }
                });
    }

    private void acceptFriendRequest() {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        addIdValueToEachUserFriend();
        senderFriendRef.child(receiverUserId).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            receiverFriendRef.child(senderUserId).child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                friendRequestRef.child(senderUserId).child(receiverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    friendRequestRef.child(receiverUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
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

    private void cancelFriendRequest() {
        friendRequestRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            friendRequestRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendFriendRequestBtn.setEnabled(true);
                                                currentState = "not_friends";
                                                sendFriendRequestBtn.setText("Send Friend Request");

                                                declineFriendRequestBtn.setVisibility(View.INVISIBLE);
                                                declineFriendRequestBtn.setEnabled(false);
                                            }
                                        }
                                    });

                        }
                    }
                });
    }

    private void maintenanceOfButton() {
        friendRequestRef.child(senderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(receiverUserId)) {
                            String request_type = snapshot.child(receiverUserId).child("request_type").getValue().toString();

                            if (request_type.equals("sent")) {
                                currentState = "request_sent";
                                sendFriendRequestBtn.setText("Cancel friend request");

                                declineFriendRequestBtn.setVisibility(View.INVISIBLE);
                                declineFriendRequestBtn.setEnabled(false);

                                blockFriendBtn.setVisibility(View.INVISIBLE);
                                blockFriendBtn.setEnabled(false);

                            } else if (request_type.equals("received")) {
                                currentState = "request_received";
                                sendFriendRequestBtn.setText("Accept friend request");

                                declineFriendRequestBtn.setVisibility(View.VISIBLE);
                                declineFriendRequestBtn.setEnabled(true);

                                blockFriendBtn.setVisibility(View.INVISIBLE);
                                blockFriendBtn.setEnabled(false);

                                declineFriendRequestBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        cancelFriendRequest();
                                    }
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

    //For sender block
    private void validBeBlockedFriend() {
        senderFriendRef.child(receiverUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("status")) {
                    String status = snapshot.child("status").getValue().toString();
                    if (status.equals("Blocked")) {
                        currentState = "blocked";
                        sendFriendRequestBtn.setVisibility(View.INVISIBLE);
                        sendFriendRequestBtn.setEnabled(false);

                        declineFriendRequestBtn.setVisibility(View.INVISIBLE);
                        declineFriendRequestBtn.setEnabled(false);

                        blockFriendBtn.setVisibility(View.VISIBLE);
                        blockFriendBtn.setEnabled(true);
                        blockFriendBtn.setText("UnBlocked");
                        isBlocked = true;

                        blockFriendBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                isBlocked = false;
                                unblockFriend();

                            }
                        });
                    }
                    if (status.equals("beBlocked")) {
                        currentState = "blocked";
                        sendFriendRequestBtn.setVisibility(View.INVISIBLE);
                        sendFriendRequestBtn.setEnabled(false);

                        declineFriendRequestBtn.setVisibility(View.INVISIBLE);
                        declineFriendRequestBtn.setEnabled(false);

                        blockFriendBtn.setVisibility(View.VISIBLE);
                        blockFriendBtn.setEnabled(false);
                        blockFriendBtn.setText("You are be blocked");
                        isBlocked = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendFriendRequestToPerson() {
        friendRequestRef.child(senderUserId).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            friendRequestRef.child(receiverUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendFriendRequestBtn.setEnabled(true);
                                                currentState = "request_sent";
                                                sendFriendRequestBtn.setText("Cancel Sent Request");

                                                declineFriendRequestBtn.setVisibility(View.INVISIBLE);
                                                declineFriendRequestBtn.setEnabled(false);
                                            }
                                        }
                                    });

                        }
                    }
                });
    }

    private void backToFriendState() {
        sendFriendRequestBtn.setEnabled(true);
        sendFriendRequestBtn.setVisibility(View.VISIBLE);
        currentState = "friends";
        sendFriendRequestBtn.setText("Unfriend this person");

        declineFriendRequestBtn.setVisibility(View.INVISIBLE);
        declineFriendRequestBtn.setEnabled(false);

        //TODO
        blockFriendBtn.setVisibility(View.VISIBLE);
        blockFriendBtn.setEnabled(true);
        blockFriendBtn.setText("Blocked");
        blockFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blockFriendBtn.setEnabled(false);
                currentState = "blocked";
                blockFriend();
            }
        });
    }

    private void addIdValueToEachUserFriend() {
        senderFriendRef.child(receiverUserId).child("id").setValue(receiverUserId);
        receiverFriendRef.child(senderUserId).child("id").setValue(senderUserId);
    }

    private void initializeFields() {
        userName = (TextView) findViewById(R.id.person_username);
        userProfileName = (TextView) findViewById(R.id.person_profile_full_name);
        userStatus = (TextView) findViewById(R.id.person_profile_status);
        userCountry = (TextView) findViewById(R.id.person_country);
        userGender = (TextView) findViewById(R.id.person_gender);
        userDOB = (TextView) findViewById(R.id.person_dob);
        userProfileImage = (CircleImageView) findViewById(R.id.person_profile_pic);
        sendFriendRequestBtn = (Button) findViewById(R.id.person_send_friend_request_btn);
        declineFriendRequestBtn = (Button) findViewById(R.id.person_decline_friend_request_btn);
        blockFriendBtn = (Button) findViewById(R.id.person_block_friend_btn);
        currentState = "not_friends";
    }


}