package com.example.mapsgt.ui.add_friend;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapsgt.R;
import com.example.mapsgt.adapter.FriendAdapter;
import com.example.mapsgt.data.dao.UserDAO;
import com.example.mapsgt.data.entities.User;
import com.example.mapsgt.enumeration.UserGenderEnum;
import com.example.mapsgt.friends.PersonProfileActivity;
import com.example.mapsgt.ui.add_friend.find_friend.FindUserFragment;
import com.example.mapsgt.ui.base.BaseActivity;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddFriendActivity extends BaseActivity implements FriendAdapter.OnFriendsDetailListener {
    private SearchView svUserSearch;
    private RecyclerView rvListSuggestUsers;
    public static String visit_user_id;
    private final UserDAO userDAO = new UserDAO(FirebaseDatabase.getInstance().getReference("users"));
    private ArrayList<User> suggest_list = new ArrayList<>();
    private ArrayList<User> filtered_list = new ArrayList<>();
    private User currentUser;

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
        FriendAdapter suggestFriendAdapter = new FriendAdapter(filtered_list, AddFriendActivity.this);
        rvListSuggestUsers.setAdapter(suggestFriendAdapter);

        // Search user
        svUserSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                replaceFragment(getLayoutResource(), FindUserFragment.newInstance(newText));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtered_list.clear();
                if (newText.isEmpty()) {
                    filtered_list.addAll(suggest_list);
                } else {
                    for (User user : suggest_list) {
                        if (user.getLastName().toLowerCase().contains(newText.toLowerCase())) {
                            filtered_list.add(user);
                        }
                        if (user.getFirstName().toLowerCase().contains(newText.toLowerCase())) {
                            filtered_list.add(user);
                        }
                    }
                }
                suggestFriendAdapter.notifyDataSetChanged();
                return true;
            }
        });
        rvListSuggestUsers.setLayoutManager(new LinearLayoutManager(this));

        // TODO: get current user
        userDAO.getSuggestedUsers(10.838665, 106.6652783).observe(this, users -> {
            suggest_list.clear();
            filtered_list.clear();
            suggest_list.addAll(users);
            filtered_list.addAll(users);
            suggestFriendAdapter.notifyDataSetChanged();
        });
    }

    //    latitude:10.838665
//    longitude:106.6652783
    public void insertUserIn10km() {
        for (int i = 0; i < 2; i++) {
            String id = "user" + i;
            String email = "user" + i + "@example.com";
            String phone = "098765432" + i;
            String firstName = "User " + i;
            String lastName = "Last Name";
            String dateOfBirth = "Jan 1, 1990 12:00:00 AM";
            UserGenderEnum gender = UserGenderEnum.MALE;
            double latitude = 10.838665 + (Math.random() * 0.1 - 0.05);
            double longitude = 106.6652783 + (Math.random() * 0.1 - 0.05);
            boolean isSharing = Math.random() < 0.5;

            // Create the user object
            User user = new User(id, email, phone, firstName, lastName, dateOfBirth, gender, latitude, longitude, isSharing);

            userDAO.insert(user);

            // Set the location of the user in GeoFire
            userDAO.setLocation(id, latitude, longitude);
        }
    }

    @Override
    public int getLayoutResource() {
        return R.id.main_holder_friend;
    }

    @Override
    public void OnFriendsDetailClick(int position) {
        //visit_user_id = Integer.toString(position + 1);

        visit_user_id = filtered_list.get(position + 1).getId();
        Log.d(TAG, "ID to find: " + visit_user_id);
        //Intent intent = new Intent(this, PersonProfileActivity.class);
        //intent.putExtra("visit_user_id", visit_user_id);
        //startActivity(intent);

        Bundle bundle = new Bundle();
        bundle.putString("visit_user_id", visit_user_id);
        //TODO: add bundle to intent and start activity
    }
}