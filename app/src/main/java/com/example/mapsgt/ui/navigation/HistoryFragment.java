package com.example.mapsgt.ui.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapsgt.MainActivity;
import com.example.mapsgt.R;
import com.example.mapsgt.ui.map.HistoryPlace;
import com.example.mapsgt.ui.map.HistoryPlaceAdapter;
import com.example.mapsgt.ui.map.MapsFragment;
import com.google.android.gms.maps.MapFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private static final String TAG = "HistoryPlaceFragment";
    private TextView textView;
    private RecyclerView recyclerView;
    private List<HistoryPlace> historyPlaces = new ArrayList<>();
    private String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        textView = view.findViewById(R.id.textView);
        recyclerView = view.findViewById(R.id.rcv_list_location);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        HistoryPlaceAdapter historyPlaceAdapter = new HistoryPlaceAdapter(historyPlaces);

        FirebaseDatabase.getInstance().getReference("history_places").orderByChild("userId")
                .equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot placeSnapshot : snapshot.getChildren()) {
                            HistoryPlace place = placeSnapshot.getValue(HistoryPlace.class);
                            historyPlaces.add(place);
                        }
                        Log.d(TAG, "onDataChange: "+ historyPlaces.size());
                        historyPlaceAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Failed to get favourite places", error.toException());
                    }
                });
        recyclerView.setAdapter(historyPlaceAdapter);
        historyPlaceAdapter.setOnItemClickListener(new HistoryPlaceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                HistoryPlace clickedHistoryPlace = historyPlaces.get(position);
                MapsFragment mapFragment = new MapsFragment();

                // Pass the clicked HistoryPlace object as arguments to the MapFragment
                Bundle args = new Bundle();
                args.putParcelable("historyPlace", clickedHistoryPlace);
                mapFragment.setArguments(args);

                // Navigate to the MapFragment
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.main_holder, mapFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                NavigationView navigationView = requireActivity().findViewById(R.id.navigation_view);
                navigationView.setCheckedItem(R.id.nav_home);

            }
        });
        return view;

    }
}
