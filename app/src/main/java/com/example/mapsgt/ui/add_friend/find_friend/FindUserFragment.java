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
import com.example.mapsgt.data.entities.User;
import com.example.mapsgt.friends.PersonProfileActivity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FindUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindUserFragment extends Fragment implements FriendAdapter.OnFriendsDetailListener {
    private static final String ARG_PARAM = "query";
    public static String visit_user_id;
    private String mQuery;
    private RecyclerView rvUsers;
    private FindUserViewModel mViewModel;
    private ArrayList<User> users_list = new ArrayList<>();
    public FindUserFragment() {
        // Required empty public constructor
    }

    public static FindUserFragment newInstance(String query) {
        FindUserFragment fragment = new FindUserFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_user, container, false);
        rvUsers = view.findViewById(R.id.rcv_find_user);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        //TODO: Find user by query
        mViewModel = new FindUserViewModel();
        users_list = mViewModel.findFriend(mQuery);
        if (users_list == null) {
            users_list = new ArrayList<>();
        } else {
            users_list = mViewModel.findFriend(mQuery);
            FriendAdapter friendAdapter = new FriendAdapter(users_list, this);
            rvUsers.setAdapter(friendAdapter);
            friendAdapter.notifyDataSetChanged();
        }
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void OnFriendsDetailClick(int position) {
        visit_user_id = Integer.toString(position + 1);
        startActivity(new Intent(getContext(), PersonProfileActivity.class));
    }
}