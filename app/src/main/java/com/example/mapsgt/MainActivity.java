package com.example.mapsgt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.MenuItem;
import android.widget.Toast;

import com.example.mapsgt.map.MapsFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_FAVORITE = 1;
    private static final int FRAGMENT_HISTORY = 2;

    private int mCurrentFragment = FRAGMENT_HOME;
    private DrawerLayout mDrawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapsFragment mapsFragment = new MapsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_holder_map, mapsFragment)
                .commit();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        replaceFragment(new MapsFragment());
        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,ACCESS_FINE_LOCATION_CODE);
    }
    private static final int ACCESS_FINE_LOCATION_CODE = 100;
    private boolean mlocationPermissionGranted = false;
    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] { permission }, requestCode);
            Toast.makeText(this, "Permission  not granted", Toast.LENGTH_SHORT).show();
        }
        else {

            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == ACCESS_FINE_LOCATION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Activity Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                System.exit(0);
                Toast.makeText(this, "Activity Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home){
            if (mCurrentFragment != FRAGMENT_HOME){
                replaceFragment(new MapsFragment());
                mCurrentFragment = FRAGMENT_HOME;
            }
        } else if (id == R.id.nav_favorite){
            if (mCurrentFragment != FRAGMENT_FAVORITE){
                replaceFragment(new MapsFragment());
                mCurrentFragment = FRAGMENT_FAVORITE;
            }
        } else if (id == R.id.nav_history){
            if (mCurrentFragment != FRAGMENT_HISTORY){
                replaceFragment(new MapsFragment());
                mCurrentFragment = FRAGMENT_HISTORY;
            }
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_holder_map, fragment);
        transaction.commit();
    }
}