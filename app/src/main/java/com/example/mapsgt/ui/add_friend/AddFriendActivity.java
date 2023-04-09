package com.example.mapsgt.ui.add_friend;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapsgt.R;
import com.example.mapsgt.adapter.FriendAdapter;
import com.example.mapsgt.data.dao.UserDAO;
import com.example.mapsgt.friends.PersonProfileActivity;
import com.example.mapsgt.ui.add_friend.find_friend.FindFriendFragment;
import com.example.mapsgt.ui.base.BaseActivity;
import com.google.firebase.database.FirebaseDatabase;

public class AddFriendActivity extends BaseActivity implements FriendAdapter.OnFriendsDetailListener {
    private SearchView svUserSearch;
    private RecyclerView rvListSuggestUsers;
    public static String visit_user_id;
    private UserDAO userDAO = new UserDAO(FirebaseDatabase.getInstance().getReference("User"));
    private void InitializeFields() {
        svUserSearch = findViewById(R.id.sv_user_search);
        rvListSuggestUsers = findViewById(R.id.rcv_list_users);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        InitializeFields();

        svUserSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                replaceFragment(getLayoutResource(), FindFriendFragment.newInstance(query));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        rvListSuggestUsers.setLayoutManager(new LinearLayoutManager(this));

        userDAO.getAll().observe(this, users -> {
            FriendAdapter suggestFriendAdapter = new FriendAdapter(users, AddFriendActivity.this);
            rvListSuggestUsers.setAdapter(suggestFriendAdapter);
        });
    }

    @Override
    public int getLayoutResource() {
        return R.id.main_holder_friend;
    }

    @Override
    public void OnFriendsDetailClick(int position) {
        visit_user_id = Integer.toString(position + 1);
        Intent intent = new Intent(this, PersonProfileActivity.class);
        intent.putExtra("visit_user_id", visit_user_id);
        startActivity(intent);
    }
}