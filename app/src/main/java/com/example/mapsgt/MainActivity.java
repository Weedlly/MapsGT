package com.example.mapsgt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.mapsgt.map.MapsFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapsFragment mapsFragment = new MapsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_holder_map, mapsFragment)
                .commit();
    }
}