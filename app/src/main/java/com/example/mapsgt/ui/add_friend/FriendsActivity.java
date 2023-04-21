package com.example.mapsgt.ui.add_friend;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapsgt.R;
import com.example.mapsgt.adapter.FriendAdapter;
import com.example.mapsgt.data.dao.UserDAO;
import com.example.mapsgt.data.entities.User;
import com.example.mapsgt.friends.PersonProfileActivity;
import com.example.mapsgt.ui.base.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FriendsActivity extends BaseActivity implements FriendAdapter.OnFriendsDetailListener {
    private static final String ARG_PARAM = "query";
    public static String visit_user_id;
    private final UserDAO friendDAO = new UserDAO(FirebaseDatabase.getInstance().getReference("users").child("O21R3tAHqjXH7uKEgUMlteCH8r03").child("Friends"));
    private RecyclerView rcvFriends;
    private ArrayList<User> filtered_list = new ArrayList<>();

    private DatabaseReference FriendsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String online_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        InitializeFields();
        FriendAdapter suggestFriendAdapter = new FriendAdapter(filtered_list, FriendsActivity.this);
        rcvFriends.setAdapter(suggestFriendAdapter);

        friendDAO.getAll().observe(this, users -> {
            filtered_list.clear();
            filtered_list.addAll(users);
            suggestFriendAdapter.notifyDataSetChanged();
        });
        //DisplayAllFriends();
    }

    private void DisplayAllFriends() {

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
        visit_user_id = filtered_list.get(position).getId();

        Intent intent = new Intent(FriendsActivity.this, PersonProfileActivity.class);
        intent.putExtra("visit_user_id", visit_user_id);
        startActivity(intent);
    }
}
