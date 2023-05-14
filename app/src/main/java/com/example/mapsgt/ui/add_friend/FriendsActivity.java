package com.example.mapsgt.ui.add_friend;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapsgt.R;
import com.example.mapsgt.adapter.FriendAdapter;
import com.example.mapsgt.data.dao.FriendRelationshipDAO;
import com.example.mapsgt.data.dao.UserDAO;
import com.example.mapsgt.data.entities.Friend;
import com.example.mapsgt.data.entities.User;
import com.example.mapsgt.ui.friends.PersonProfileActivity;
import com.example.mapsgt.ui.base.BaseActivity;
import com.example.mapsgt.ui.communicate.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class FriendsActivity extends BaseActivity implements FriendAdapter.OnFriendsDetailListener {
    private static final String TAG = "FriendsActivity";
    private FirebaseAuth mAuth;
    public static String visitUserId;
    public static String userName;
    String senderId;
    private FriendRelationshipDAO friendRelationshipDAO;
    private UserDAO userDAO;
    private RecyclerView friendsRcv;

    private ArrayList<User> userList;
    private ArrayList<Friend> friendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mAuth = FirebaseAuth.getInstance();
        senderId = mAuth.getCurrentUser().getUid();

        userDAO = new UserDAO();
        friendRelationshipDAO = new FriendRelationshipDAO();

        userList = new ArrayList<>();
        friendList = new ArrayList<>();

        initializeFields();

        friendList.clear();
        userList.clear();

        friendRelationshipDAO.getFriendList(senderId).observe(this, friends -> {
            friendList.addAll(friends);
            for (Friend friend : friendList) {
                userDAO.getByKey(friend.getId()).observe(this, user -> {
                    userList.add(user);
                    FriendAdapter suggestFriendAdapter = new FriendAdapter(this, userList, FriendsActivity.this);
                    friendsRcv.setAdapter(suggestFriendAdapter);
                });
            }
        });
    }

    private void initializeFields() {
        friendsRcv = findViewById(R.id.friend_list);
        friendsRcv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        friendsRcv.setLayoutManager(linearLayoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        friendsRcv.addItemDecoration(itemDecoration);
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
