package com.example.mapsgt.ui.add_friend;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapsgt.R;
import com.example.mapsgt.adapter.FriendAdapter;
import com.example.mapsgt.data.dao.UserDAO;
import com.example.mapsgt.data.entities.User;
import com.example.mapsgt.enumeration.UserGenderEnum;
import com.example.mapsgt.ui.friends.PersonProfileActivity;
import com.example.mapsgt.ui.add_friend.find_friend.FindUserFragment;
import com.example.mapsgt.ui.base.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddFriendActivity extends BaseActivity implements FriendAdapter.OnFriendsDetailListener {
    private SearchView svUserSearch;
    private RecyclerView rvListSuggestUsers;
    public static String visitUserId;
    private UserDAO userDAO;
    private ArrayList<User> suggestList = new ArrayList<>();
    private ArrayList<User> filteredList = new ArrayList<>();
    private User currentUser;

    // Initialize fields
    private void initializeFields() {
        svUserSearch = findViewById(R.id.sv_user_search);
        rvListSuggestUsers = findViewById(R.id.rcv_list_users);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userDAO = new UserDAO(FirebaseDatabase.getInstance().getReference("users"));

        initializeFields();
        FriendAdapter suggestFriendAdapter = new FriendAdapter(filteredList, AddFriendActivity.this);
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
                filteredList.clear();
                if (newText.isEmpty()) {
                    filteredList.addAll(suggestList);
                } else {
                    for (User user : suggestList) {
                        if (user.getLastName().toLowerCase().contains(newText.toLowerCase())) {
                            filteredList.add(user);
                        }
                        if (user.getFirstName().toLowerCase().contains(newText.toLowerCase())) {
                            filteredList.add(user);
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
            suggestList.clear();
            filteredList.clear();
            suggestList.addAll(users);
            filteredList.addAll(users);
            suggestFriendAdapter.notifyDataSetChanged();
        });
    }

    public void insertUserIn10km() {
        for (int i = 0; i < 4; i++) {
            String email = "user" + i + "@example.com";
            String phoneNo = "098765432" + i;
            String firstName = "User " + i;
            String lastName = "Last Name";
            String dob = "Jan 1, 1990";
            String profilePicture = "https://raw.githubusercontent.com/gotitinc/aha-assets/master/uifaces/m-10.jpg";
            UserGenderEnum gender = UserGenderEnum.MALE;
            double latitude = 10.838665 + (Math.random() * 0.1 - 0.05);
            double longitude = 106.6652783 + (Math.random() * 0.1 - 0.05);
            boolean isSharing = Math.random() < 0.5;

            String password = "123456";
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser authUser = mAuth.getCurrentUser();

                                if (authUser != null) {
                                    User user = new User(authUser.getUid(), authUser.getEmail(), phoneNo, firstName, lastName, dob, gender, latitude, longitude, true, profilePicture);
                                    userDAO.insert(user);
                                    // Set the location of the user in GeoFire
                                    userDAO.setLocation(authUser.getUid(), latitude, longitude);
                                }
                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            }
                        }
                    });
        }
    }

    @Override
    public int getLayoutResource() {
        return R.id.main_holder_friend;
    }

    @Override
    public void onFriendsDetailClick(int position) {
        visitUserId = filteredList.get(position).getId();

        Intent intent = new Intent(AddFriendActivity.this, PersonProfileActivity.class);
        intent.putExtra("visit_user_id", visitUserId);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}