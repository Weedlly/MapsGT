package com.example.mapsgt.ui.add_friend.find_friend;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapsgt.R;
import com.example.mapsgt.adapter.FriendAdapter;
import com.example.mapsgt.data.dao.UserDAO;
import com.example.mapsgt.data.entities.User;
import com.example.mapsgt.ui.friends.PersonProfileActivity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FoundUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FoundUserFragment extends Fragment implements FriendAdapter.OnFriendsDetailListener {
    private static final String ARG_PARAM = "query";
    private static final String TAG = "FindUserFragment";
    public static String visitUserId;
    private String mQuery;
    private RecyclerView rvUsers;
    private ArrayList<User> usersList = new ArrayList<>();
    private UserDAO userDAO;

    public FoundUserFragment() {
        // Required empty public constructor
    }

    public static FoundUserFragment newInstance(String query) {
        FoundUserFragment fragment = new FoundUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuery = getArguments().getString(ARG_PARAM);
        }
        userDAO = new UserDAO();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_found_user_list, container, false);
        rvUsers = view.findViewById(R.id.rcv_find_user);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));

        userDAO.getByEmail(mQuery).observe(getViewLifecycleOwner(), userRes -> {
            if (userRes != null) {
                usersList.clear();
                usersList.add(userRes);
                FriendAdapter friendAdapter = new FriendAdapter(getContext(), usersList, this);
                rvUsers.setAdapter(friendAdapter);
            }
        });

        userDAO.getByPhone(mQuery).observe(getViewLifecycleOwner(), userRes -> {
            if (userRes != null) {
                usersList.clear();
                usersList.add(userRes);
                FriendAdapter friendAdapter = new FriendAdapter(getContext(), usersList, this);
                rvUsers.setAdapter(friendAdapter);
            }
        });
        return view;
    }

    @Override
    public void onFriendsDetailClick(int position) {
        visitUserId = usersList.get(position).getId();

        Intent intent = new Intent(getContext(), PersonProfileActivity.class);
        intent.putExtra("visit_user_id", visitUserId);
        startActivity(intent);
    }
}