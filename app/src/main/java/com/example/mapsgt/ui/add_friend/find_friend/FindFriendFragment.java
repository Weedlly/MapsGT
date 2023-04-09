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
import com.example.mapsgt.friends.PersonProfileActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FindFriendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindFriendFragment extends Fragment implements FriendAdapter.OnFriendsDetailListener {
    private static final String ARG_PARAM = "query";
    public static String visit_user_id;
    private String mQuery;
    private RecyclerView rvUsers;
    public FindFriendFragment() {
        // Required empty public constructor
    }

    public static FindFriendFragment newInstance(String query) {
        FindFriendFragment fragment = new FindFriendFragment();
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
        View view = inflater.inflate(R.layout.fragment_find_friend, container, false);
        rvUsers = view.findViewById(R.id.rcv_find_friend);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void OnFriendsDetailClick(int position) {
        visit_user_id = Integer.toString(position + 1);
        Intent intent = new Intent(getContext(), PersonProfileActivity.class);
        intent.putExtra("visit_user_id", visit_user_id);
        startActivity(intent);
    }
}