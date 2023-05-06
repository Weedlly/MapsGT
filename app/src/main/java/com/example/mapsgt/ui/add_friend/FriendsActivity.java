package com.example.mapsgt.ui.add_friend;


import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapsgt.R;
import com.example.mapsgt.adapter.FriendAdapter;
import com.example.mapsgt.data.dao.FriendDAO;
import com.example.mapsgt.data.dao.UserDAO;
import com.example.mapsgt.data.entities.Friend;
import com.example.mapsgt.data.entities.User;
import com.example.mapsgt.friends.PersonProfileActivity;
import com.example.mapsgt.ui.base.BaseActivity;
import com.example.mapsgt.ui.communicate.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendsActivity extends BaseActivity implements FriendAdapter.OnFriendsDetailListener {
    private static final String ARG_PARAM = "query";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static String visit_user_id;
    public static String userName;

    //TODO: change mAuth.getCurrentUser().getUid();
    String senderID = "v63SkgeB9nXSmm3aICpri24mWfj1";
    private final FriendDAO friendDAO = new FriendDAO(FirebaseDatabase.getInstance().getReference("FriendRelationship").child(senderID).child("Friends"));
    private final UserDAO userDAO = new UserDAO(FirebaseDatabase.getInstance().getReference("users"));
    private RecyclerView rcvFriends;


    private ArrayList<User> users_list = new ArrayList<>();
    private ArrayList<Friend> friend_list = new ArrayList<>();

    private final MutableLiveData<ArrayList<User>> friendsLiveData = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        InitializeFields();

        friend_list.clear();
        users_list.clear();
        friendDAO.getAll().observe(this, Friend -> {
            friend_list.addAll(Friend);

            for (Friend friend : friend_list) {
                userDAO.getUserById(friend.getID()).observe(this, user -> {
                    users_list.add(user);
                });
            }

            FriendAdapter suggestFriendAdapter = new FriendAdapter(users_list, FriendsActivity.this);
            rcvFriends.setAdapter(suggestFriendAdapter);
        });
    }

    private void InitializeFields() {

        rcvFriends = findViewById(R.id.friend_list);
        rcvFriends.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvFriends.setLayoutManager(linearLayoutManager);
    }

    @Override
    public int getLayoutResource() {
        return R.id.main_holder_friend;
    }
    @Override
    public void OnFriendsDetailClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
        builder.setTitle("Select option:");
        String[] options =  {    users_list.get(position).getFirstName() + "'s Profile",
                                "Send Message"
                            };

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which == 0)
                {
                    visit_user_id = users_list.get(position).getId();
                    Intent profileIntent = new Intent(FriendsActivity.this, PersonProfileActivity.class);
                    profileIntent.putExtra("visit_user_id", visit_user_id);
                    startActivity(profileIntent);
                }
                if (which == 1)
                {
                    visit_user_id = users_list.get(position).getId();
                    userName = users_list.get(position).getFirstName() + users_list.get(position).getLastName();
                    Intent chatIntent = new Intent(FriendsActivity.this, ChatActivity.class);
                    chatIntent.putExtra("visit_user_id", visit_user_id);
                    chatIntent.putExtra("userName", userName);
                    startActivity(chatIntent);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
