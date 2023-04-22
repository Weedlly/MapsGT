package com.example.mapsgt.ui.add_friend;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
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
    public static String visit_user_id;

    String senderID = "v63SkgeB9nXSmm3aICpri24mWfj1";
    private final FriendDAO friendDAO = new FriendDAO(FirebaseDatabase.getInstance().getReference("users").child(senderID).child("Friends"));
    private final UserDAO userDAO = new UserDAO(FirebaseDatabase.getInstance().getReference("users"));
    private RecyclerView rcvFriends;
    private ArrayList<Friend> filtered_list = new ArrayList<>();

    private ArrayList<User> users_list = new ArrayList<>();
    private ArrayList<Friend> friend_list = new ArrayList<>();

    private final MutableLiveData<ArrayList<User>> friendsLiveData = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        InitializeFields();
        FriendAdapter suggestFriendAdapter = new FriendAdapter(users_list, FriendsActivity.this);
        rcvFriends.setAdapter(suggestFriendAdapter);

        friendDAO.getAll().observe(this, Friend -> {
            filtered_list.clear();
            filtered_list.addAll(Friend);
            Log.d(TAG,"User:" + filtered_list.get(0).getID());
        });

        users_list.clear();
        for (Friend friend : filtered_list) {
            userDAO.getUserById(friend.getID()).observe(this, user -> {
                users_list.add(user);

            });
        }
        Log.d(TAG,"User List: " +  users_list.toString());
        //suggestFriendAdapter.notifyDataSetChanged();



    }

    private void InitializeFields() {

        rcvFriends = (RecyclerView) findViewById(R.id.friend_list);
        rcvFriends.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rcvFriends.setLayoutManager(linearLayoutManager);
    }

    @Override
    public int getLayoutResource() {
        return R.id.main_holder_friend;
    }
    @Override
    public void OnFriendsDetailClick(int position) {
        visit_user_id = filtered_list.get(position).getID();
        Log.d(TAG, "Visit: "+ visit_user_id);
        Intent intent = new Intent(FriendsActivity.this, PersonProfileActivity.class);
        intent.putExtra("visit_user_id", visit_user_id);
        startActivity(intent);
    }
}
