package com.example.mapsgt.friends;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapsgt.R;
import com.example.mapsgt.data.entities.User;

import java.util.ArrayList;
import java.util.List;

public class FindFriends extends AppCompatActivity {
    private RecyclerView rcvUsers;
    private SearchFriendAdapter searchFriendAdapter;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        rcvUsers = findViewById(R.id.rcv_user);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvUsers.setLayoutManager(linearLayoutManager);

        searchFriendAdapter = new SearchFriendAdapter(getListUsers());
        rcvUsers.setAdapter(searchFriendAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvUsers.addItemDecoration(itemDecoration);

        //todo
       // SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        //searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFriendAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchFriendAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private List<User> getListUsers() {
        List<User> list = new ArrayList<>();
        list.add(new User("1", "testing01@gmail.com", "012345678", "Duy01", "Alann", null, null));
        list.add(new User("2", "testing02@gmail.com", "012845678", "B02", "Run", null, null));
        list.add(new User("3", "testing03@gmail.com", "012345678", "C03", "Fire", null, null));
        list.add(new User("4", "testing04@gmail.com", "012965678", "D04", "Brown", null, null));
        list.add(new User("5", "testing05@gmail.com", "012345678", "E05", "Billy", null, null));
        list.add(new User("6", "testing06@gmail.com", "013745678", "F06", "Fear", null, null));
        list.add(new User("7", "testing07@gmail.com", "012355578", "G07", "Google", null, null));
        list.add(new User("8", "testing08@gmail.com", "012349998", "H08", "Day", null, null));
        list.add(new User("9", "testing08@gmail.com", "012341018", "L09", "Simon", null, null));

        return list;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//
//        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setMaxWidth(Integer.MAX_VALUE);
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                searchFriendAdapter.getFilter().filter(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                searchFriendAdapter.getFilter().filter(newText);
//                return false;
//            }
//        });
//        return true;
//    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        }
        super.onBackPressed();
    }
}
