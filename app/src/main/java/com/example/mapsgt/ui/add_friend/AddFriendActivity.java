package com.example.mapsgt.ui.add_friend;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapsgt.R;
import com.example.mapsgt.adapter.FriendAdapter;
import com.example.mapsgt.data.dao.UserDAO;
import com.example.mapsgt.data.entities.User;
import com.example.mapsgt.ui.add_friend.find_friend.FindUserFragment;
import com.example.mapsgt.ui.base.BaseActivity;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddFriendActivity extends BaseActivity implements FriendAdapter.OnFriendsDetailListener {
    private SearchView svUserSearch;
    private RecyclerView rvListSuggestUsers;
    public static String visit_user_id;
    private final UserDAO userDAO = new UserDAO(FirebaseDatabase.getInstance().getReference("users"));
    // Initialize fields
    private void InitializeFields() {
        svUserSearch = findViewById(R.id.sv_user_search);
        rvListSuggestUsers = findViewById(R.id.rcv_list_users);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        InitializeFields();
        // Search user
        svUserSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                replaceFragment(getLayoutResource(), FindUserFragment.newInstance(query));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        // Suggest user
        rvListSuggestUsers.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<User> users_list = new ArrayList<>();
        userDAO.getAll().observe(this, users -> {
            users_list.clear();
            users_list.addAll(users);
            FriendAdapter suggestFriendAdapter = new FriendAdapter(users_list, AddFriendActivity.this);
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
        Bundle bundle = new Bundle();
        bundle.putString("visit_user_id", visit_user_id);
        //TODO: add bundle to intent and start activity
    }
}