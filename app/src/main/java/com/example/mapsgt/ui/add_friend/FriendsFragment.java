package com.example.mapsgt.ui.add_friend;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapsgt.R;

public class FriendsFragment extends Fragment {
    private static final String ARG_PARAM = "query";
    private RecyclerView rvFriends;

    private String mQuery;

    public FriendsFragment(){

    }

    public static FriendsFragment newInstance(String query) {
        FriendsFragment fragment = new FriendsFragment();
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
        View view = inflater.inflate(R.layout.activity_friends, container, false);
        rvFriends = view.findViewById(R.id.friend_list);
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    private void DisplayAllFriends() {

    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        public FriendsViewHolder(View itemView)
        {
            super(itemView);

            mView = itemView;
        }
    }
}
