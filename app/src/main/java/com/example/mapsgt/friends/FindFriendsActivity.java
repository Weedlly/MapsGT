package com.example.mapsgt.friends;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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

public class FindFriendsActivity extends AppCompatActivity implements FriendAdapter.OnFriendsDetailListener{
    private RecyclerView mRecyclerView;

    public static ArrayList<User> mListFriends = new ArrayList<>();

    private FriendAdapter mListFriendRecyclerAdapter;
    //private personAdapter mListPersonAdapter;

    private SearchView searchView;

    FirebaseDatabase ListUserData = FirebaseDatabase.getInstance();
    private DatabaseReference allUserDatabaseRef = ListUserData.getReference();

    public static String visit_user_id;

    private DatabaseReference allUserFromDatabaseRef = ListUserData.getReference("User");


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        //mListFriends = getListUsers();
       //UpDataToFirebase();


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
                //mListFriends.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    User user = dataSnapshot.getValue(User.class);
                    mListFriends.add(user);
                    //Log.d(TAG, mListFriends.toString());

                    mListFriendRecyclerAdapter = new FriendAdapter( mListFriends, FindFriendsActivity.this);
                    mRecyclerView.setAdapter(mListFriendRecyclerAdapter);
                    SearchData();
                }

                //RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(FindFriendsActivity.this, DividerItemDecoration.VERTICAL);
                //mRecyclerView.addItemDecoration(itemDecoration);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SearchData() {
        searchView = findViewById(R.id.search_view);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //mListFriendRecyclerAdapter.getFilter().filter(query);

                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.d(TAG, "Debug Search View 2");
                mListFriendRecyclerAdapter.getFilter().filter(newText);
                Log.d(TAG, "queryData: " + mListFriendRecyclerAdapter.getFilter());

                //mListFriends.clear();

               // mRecyclerView.setAdapter(mListFriendRecyclerAdapter);

                Query queryData = FirebaseDatabase.getInstance().getReference("User").orderByChild("firstName").equalTo(newText);

                queryData.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mListFriends.clear();
                        //Log.d(TAG,"Show seach view");
                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                        {
                            User user = dataSnapshot.getValue(User.class);
                            mListFriends.add(user);
                            //Log.d(TAG, mListFriends.toString());
                            Log.d(TAG, "List friend search: " + mListFriends.toString());

                            mListFriendRecyclerAdapter = new FriendAdapter( mListFriends, FindFriendsActivity.this);
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
    public void OnFriendsDetailClick(int position) {
       // Log.d(TAG, "onFriendDetailClick: clicker." + position);

        visit_user_id = Integer.toString(position + 1);
        Intent intent = new Intent(this, PersonProfileActivity.class);
        intent.putExtra("visit_user_id", visit_user_id);
        startActivity(intent);
    }
}
