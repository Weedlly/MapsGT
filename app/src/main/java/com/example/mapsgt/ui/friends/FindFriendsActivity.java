package com.example.mapsgt.ui.friends;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapsgt.R;
import com.example.mapsgt.adapter.FriendAdapter;
import com.example.mapsgt.data.entities.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindFriendsActivity extends AppCompatActivity implements FriendAdapter.OnFriendsDetailListener {
    private RecyclerView mRecyclerView;

    public static ArrayList<User> mListFriends = new ArrayList<>();

    private FriendAdapter mListFriendRecyclerAdapter;

    private SearchView searchView;

    FirebaseDatabase ListUserData = FirebaseDatabase.getInstance();
    private DatabaseReference allUserDatabaseRef = ListUserData.getReference();

    public static String visitUserId;

    private DatabaseReference allUserFromDatabaseRef = ListUserData.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        initRecyclerView();
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        }
        super.onBackPressed();
    }

    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.search_result_list);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        allUserFromDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    mListFriends.add(user);

                    mListFriendRecyclerAdapter = new FriendAdapter(getApplicationContext(), mListFriends, FindFriendsActivity.this);
                    mRecyclerView.setAdapter(mListFriendRecyclerAdapter);
                    searchData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO: Handle error
            }
        });
    }

    private void searchData() {
        searchView = findViewById(R.id.search_view);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mListFriendRecyclerAdapter.getFilter().filter(newText);

                Query queryData = allUserFromDatabaseRef.orderByChild("firstName").equalTo(newText);
                queryData.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mListFriends.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            User user = dataSnapshot.getValue(User.class);
                            mListFriends.add(user);

                            mListFriendRecyclerAdapter = new FriendAdapter(getApplicationContext(), mListFriends, FindFriendsActivity.this);
                            mRecyclerView.setAdapter(mListFriendRecyclerAdapter);
                        }

                        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(FindFriendsActivity.this, DividerItemDecoration.VERTICAL);
                        mRecyclerView.addItemDecoration(itemDecoration);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                return false;
            }
        });

    }

    @Override
    public void onFriendsDetailClick(int position) {
        visitUserId = mListFriends.get(position + 1).getId();
        Intent intent = new Intent(this, PersonProfileActivity.class);
        intent.putExtra("visit_user_id", visitUserId);
        startActivity(intent);
    }
}
