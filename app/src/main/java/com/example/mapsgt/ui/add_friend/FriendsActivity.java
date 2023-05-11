package com.example.mapsgt.ui.add_friend;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapsgt.R;
import com.example.mapsgt.adapter.FriendAdapter;
import com.example.mapsgt.data.dao.FriendDAO;
import com.example.mapsgt.data.dao.UserDAO;
import com.example.mapsgt.data.entities.Friend;
import com.example.mapsgt.data.entities.User;
import com.example.mapsgt.ui.friends.PersonProfileActivity;
import com.example.mapsgt.ui.base.BaseActivity;
import com.example.mapsgt.ui.communicate.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class FriendsActivity extends BaseActivity implements FriendAdapter.OnFriendsDetailListener {
    private static final String ARG_PARAM = "query";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static String visitUserId;
    public static String userName;

    String senderId = mAuth.getCurrentUser().getUid();
    private final FriendDAO friendDAO = new FriendDAO(FirebaseDatabase.getInstance().getReference("FriendRelationship").child(senderId).child("Friends"));
    private final UserDAO userDAO = new UserDAO(FirebaseDatabase.getInstance().getReference("users"));
    private RecyclerView friendsRcv;

    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<Friend> friendList = new ArrayList<>();

    private final MutableLiveData<ArrayList<User>> friendsLiveData = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        initializeFields();

        friendList.clear();
        userList.clear();
        friendDAO.getAll().observe(this, Friend -> {
            friendList.addAll(Friend);

            for (Friend friend : friendList) {
                userDAO.getUserById(friend.getId()).observe(this, user -> {
                    userList.add(user);
                });
            }

            FriendAdapter suggestFriendAdapter = new FriendAdapter(userList, FriendsActivity.this);
            friendsRcv.setAdapter(suggestFriendAdapter);
        });
    }

    private void initializeFields() {
        friendsRcv = findViewById(R.id.friend_list);
        friendsRcv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        friendsRcv.setLayoutManager(linearLayoutManager);
    }

    @Override
    public int getLayoutResource() {
        return R.id.main_holder_friend;
    }

    @Override
    public void onFriendsDetailClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
        builder.setTitle("Select option:");
        String[] options = {userList.get(position).getFirstName() + "'s Profile",
                "Send Message"
        };

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which == 0) {
                    visitUserId = userList.get(position).getId();
                    Intent profileIntent = new Intent(FriendsActivity.this, PersonProfileActivity.class);
                    profileIntent.putExtra("visit_user_id", visitUserId);
                    startActivity(profileIntent);
                }
                if (which == 1) {
                    visitUserId = userList.get(position).getId();
                    userName = userList.get(position).getFirstName() + userList.get(position).getLastName();
                    Intent chatIntent = new Intent(FriendsActivity.this, ChatActivity.class);
                    chatIntent.putExtra("visit_user_id", visitUserId);
                    chatIntent.putExtra("userName", userName);
                    startActivity(chatIntent);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
