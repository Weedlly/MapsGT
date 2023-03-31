package com.example.mapsgt;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.mapsgt.ui.navigation.FavoriteFragment;
import com.example.mapsgt.ui.navigation.HistoryFragment;
import com.example.mapsgt.ui.navigation.HomeFragment;
import com.example.mapsgt.ui.auth.AuthActivity;
import com.example.mapsgt.ui.base.BaseActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_FAVORITE = 1;
    private static final int FRAGMENT_HISTORY = 2;

    private static final int LOGOUT = 7;

    private int mCurrentFragment = FRAGMENT_HOME;
    private DrawerLayout mDrawerLayout;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onStart() {
        super.onStart();
        user = auth.getCurrentUser();
        if(user == null) {
            startActivity(AuthActivity.class);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        //ImageButton btn_navigation = findViewById(R.id.btn_navigation);
        Toolbar toolbar = findViewById(R.id.top_app_bar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        HomeFragment homeFragment = new HomeFragment();
        replaceFragment(getLayoutResource(),homeFragment);
        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home){
            if (mCurrentFragment != FRAGMENT_HOME){
                HomeFragment homeFragment = new HomeFragment();
                replaceFragment(getLayoutResource(),homeFragment);
                mCurrentFragment = FRAGMENT_HOME;
            }
        } else if (id == R.id.nav_favorite){
            if (mCurrentFragment != FRAGMENT_FAVORITE){
                FavoriteFragment favoriteFragment = new FavoriteFragment();
                replaceFragment(getLayoutResource(), favoriteFragment);
                mCurrentFragment = FRAGMENT_FAVORITE;
            }
        } else if (id == R.id.nav_history){
            if (mCurrentFragment != FRAGMENT_HISTORY){
                HistoryFragment historyFragment = new HistoryFragment();
                replaceFragment(getLayoutResource(), historyFragment);
                mCurrentFragment = FRAGMENT_HISTORY;
            }
        } else if (id == R.id.nav_logout){
            if (mCurrentFragment != LOGOUT){
                auth.signOut();
                startActivity(AuthActivity.class);
                finish();
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
    @Override
    public int getLayoutResource() {
        return R.id.main_holder;
    }
}