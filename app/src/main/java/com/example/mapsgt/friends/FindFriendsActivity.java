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
import com.example.mapsgt.data.entities.User;

import com.example.mapsgt.enumeration.UserGenderEnum;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FindFriendsActivity extends AppCompatActivity implements FriendsRecyclerAdapter.OnFriendsDetailListener{
    private RecyclerView mRecyclerView;

    public static ArrayList<User> mListFriends = new ArrayList<>();

    private FriendsRecyclerAdapter mListFriendRecyclerAdapter;

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

    private ArrayList<User> getListUsers() {
        ArrayList<User> list_friend = new ArrayList<>();
        list_friend.add(new User("1", "testing01@gmail.com", "012345678", "Duy01", "Alann", null, UserGenderEnum.MALE));
        list_friend.add(new User("2", "testing02@gmail.com", "012845678", "B02", "Run", null, UserGenderEnum.MALE));
        list_friend.add(new User("3", "testing03@gmail.com", "012345678", "C03", "Fire", null, UserGenderEnum.FEMALE));
        list_friend.add(new User("4", "testing04@gmail.com", "012965678", "D04", "Brown", null, UserGenderEnum.FEMALE));
        list_friend.add(new User("5", "testing05@gmail.com", "012345678", "E05", "Billy", null, UserGenderEnum.OTHER));
        list_friend.add(new User("6", "testing06@gmail.com", "013745678", "F06", "Fear", null, UserGenderEnum.MALE));
        list_friend.add(new User("7", "testing07@gmail.com", "012355578", "G07", "Google", null, UserGenderEnum.MALE));
        list_friend.add(new User("8", "testing08@gmail.com", "012349998", "H08", "Day", null, UserGenderEnum.FEMALE));
        list_friend.add(new User("9", "testing08@gmail.com", "012341018", "L09", "Simon", null, UserGenderEnum.OTHER));

        return list_friend;
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

        //mListFriends = getListUsers();

        allUserFromDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    User user = dataSnapshot.getValue(User.class);
                    mListFriends.add(user);
                    //Log.d(TAG, mListFriends.toString());

                    mListFriendRecyclerAdapter = new FriendsRecyclerAdapter( mListFriends, FindFriendsActivity.this);
                    mRecyclerView.setAdapter(mListFriendRecyclerAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);

        searchView = findViewById(R.id.search_view);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "Debug Search View ");
                mListFriendRecyclerAdapter.getFilter().filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "Debug Search View 2");
                mListFriendRecyclerAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    @Override
    public void OnFriendsDetailClick(int position) {
       // Log.d(TAG, "onFriendDetailClick: clicker." + position);

        visit_user_id = Integer.toString(position);
        Intent intent = new Intent(this, PersonProfileActivity.class);
        intent.putExtra("visit_user_id", visit_user_id);
        startActivity(intent);
    }

    private void UpDataToFirebase()
    {
        HashMap<String, Object> hashMap = new HashMap<>();
        for (int i = 0; i < getListUsers().size(); i++) {
            hashMap.put( getListUsers().get(i).getId(), getListUsers().get(i));
        }

        allUserDatabaseRef.child("User").setValue(hashMap);
    }
}
