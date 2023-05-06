package com.example.mapsgt.friends;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mapsgt.R;

import com.example.mapsgt.ui.auth.AuthActivity;
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
    private Button sendFriendRequestBTN, declineFriendRequestBTN, blockFriendBTN;

    private DatabaseReference FriendRequestFef, UsersRef, FriendsFef, senderFriendRef ,receiverFriendRef;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String senderUserId, currentState, saveCurrentDate;
    private static String receiverUserId;
    private boolean isBlocked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        //TODO: change mAuth.getCurrentUser().getUid(); by below ID to testing
        //senderUserId = "v63SkgeB9nXSmm3aICpri24mWfj1";
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, AuthActivity.class));
        }
        senderUserId = mAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        receiverUserId = intent.getStringExtra("visit_user_id");

        UsersRef = FirebaseDatabase.getInstance().getReference().child("users");
        FriendsFef = FirebaseDatabase.getInstance().getReference().child("FriendRelationship");
        FriendRequestFef = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
        senderFriendRef = FriendsFef.child(senderUserId).child("Friends");
        receiverFriendRef = FriendsFef.child(receiverUserId).child("Friends");
        InitializeFields();

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
                    String personGender = "Gender: " + snapshot.child("gender").getValue().toString();

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

        declineFriendRequestBTN.setVisibility(View.INVISIBLE);
        declineFriendRequestBTN.setEnabled(false);
        blockFriendBTN.setVisibility(View.INVISIBLE);
        blockFriendBTN.setEnabled(false);

        if (!senderUserId.equals(receiverUserId)) {
            sendFriendRequestBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendFriendRequestBTN.setEnabled(false);

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
            declineFriendRequestBTN.setVisibility(View.INVISIBLE);
            sendFriendRequestBTN.setVisibility(View.INVISIBLE);
            blockFriendBTN.setVisibility(View.INVISIBLE);
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
                                                sendFriendRequestBTN.setEnabled(true);
                                                currentState = "not_friends";
                                                sendFriendRequestBTN.setText("Send Friend Request");

                                                declineFriendRequestBTN.setVisibility(View.INVISIBLE);
                                                declineFriendRequestBTN.setEnabled(false);
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
                                sendFriendRequestBTN.setText("Cancel friend request");

                                declineFriendRequestBTN.setVisibility(View.INVISIBLE);
                                declineFriendRequestBTN.setEnabled(false);

                                blockFriendBTN.setVisibility(View.INVISIBLE);
                                blockFriendBTN.setEnabled(false);

                            } else if (request_type.equals("received")) {
                                currentState = "request_received";
                                sendFriendRequestBTN.setText("Accept friend request");

                                declineFriendRequestBTN.setVisibility(View.VISIBLE);
                                declineFriendRequestBTN.setEnabled(true);

                                blockFriendBTN.setVisibility(View.INVISIBLE);
                                blockFriendBTN.setEnabled(false);

                                declineFriendRequestBTN.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        cancelFriendRequest();
                                    }
                                });
                            }
                        } else {
                            friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void validBeBlockedFriend() //For sender block
    {
        senderFriendRef.child(receiverUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("status")) {
                    String status = snapshot.child("status").getValue().toString();
                    if (status.equals("Blocked")) {
                        currentState = "blocked";
                        sendFriendRequestBTN.setVisibility(View.INVISIBLE);
                        sendFriendRequestBTN.setEnabled(false);

                        declineFriendRequestBTN.setVisibility(View.INVISIBLE);
                        declineFriendRequestBTN.setEnabled(false);

                        blockFriendBTN.setVisibility(View.VISIBLE);
                        blockFriendBTN.setEnabled(true);
                        blockFriendBTN.setText("UnBlocked");
                        isBlocked = true;

                        blockFriendBTN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                isBlocked = false;
                                unblockFriend();

                            }
                        });
                    }
                    if (status.equals("beBlocked")) {
                        currentState = "blocked";
                        sendFriendRequestBTN.setVisibility(View.INVISIBLE);
                        sendFriendRequestBTN.setEnabled(false);

                        declineFriendRequestBTN.setVisibility(View.INVISIBLE);
                        declineFriendRequestBTN.setEnabled(false);

                        blockFriendBTN.setVisibility(View.VISIBLE);
                        blockFriendBTN.setEnabled(false);
                        blockFriendBTN.setText("You are be blocked");
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
                                                sendFriendRequestBTN.setEnabled(true);
                                                currentState = "request_sent";
                                                sendFriendRequestBTN.setText("Cancel Sent Request");

                                                declineFriendRequestBTN.setVisibility(View.INVISIBLE);
                                                declineFriendRequestBTN.setEnabled(false);
                                            }
                                        }
                                    });

                        }
                    }
                });
    }

    private void backToFriendState() {
        sendFriendRequestBTN.setEnabled(true);
        sendFriendRequestBTN.setVisibility(View.VISIBLE);
        currentState = "friends";
        sendFriendRequestBTN.setText("Unfriend this person");

        declineFriendRequestBTN.setVisibility(View.INVISIBLE);
        declineFriendRequestBTN.setEnabled(false);

        blockFriendBTN.setVisibility(View.VISIBLE);
        blockFriendBTN.setEnabled(true);
        blockFriendBTN.setText("Blocked");
        blockFriendBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blockFriendBTN.setEnabled(false);
                currentState = "blocked";
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