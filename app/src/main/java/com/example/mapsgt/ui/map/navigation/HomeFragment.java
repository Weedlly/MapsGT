package com.example.mapsgt.ui.map.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.mapsgt.R;
import com.example.mapsgt.ui.map.MapsFragment;

public class HomeFragment extends Fragment {
    private Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = requireActivity().findViewById(R.id.top_app_bar);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        SearchView searchView = view.findViewById(R.id.sv_position);
        searchView.setOnSearchClickListener(v -> toolbar.setVisibility(View.GONE));
        searchView.setOnCloseListener(() -> {
            toolbar.setVisibility(View.VISIBLE);
            return false;
        });
        MapsFragment mapsFragment = new MapsFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.map_container, mapsFragment).commit();
        return view;
    }
}
