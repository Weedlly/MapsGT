package com.example.mapsgt;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.mapsgt.data.dao.UserDAO;
import com.example.mapsgt.ui.add_friend.AddFriendActivity;
import com.example.mapsgt.ui.add_friend.FriendsActivity;
import com.example.mapsgt.ui.auth.AuthActivity;
import com.example.mapsgt.ui.base.BaseActivity;
import com.example.mapsgt.ui.map.MapsFragment;
import com.example.mapsgt.ui.navigation.FavoriteFragment;
import com.example.mapsgt.ui.navigation.FeedbackActivity;
import com.example.mapsgt.ui.navigation.HistoryFragment;
import com.example.mapsgt.ui.user_profile.UserProfileActivity;
import com.example.mapsgt.utils.AccountFirebaseUtil;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int ACCESS_FINE_LOCATION_CODE = 100;
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_FAVORITE = 1;
    private static final int FRAGMENT_HISTORY = 2;
    private static final int ACTIVITY_ADD_FRIEND = 3;
    private static final int ACTIVITY_FEEDBACK = 4;
    private static final int ACTIVITY_PROFILE = 5;
    private static final int ACTIVITY_CHAT = 6;
    private static final int LOGOUT = 7;
    private static final String TAG = "MainActivity";

    private int mCurrentFragment = FRAGMENT_HOME;
    private DrawerLayout mDrawerLayout;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private TextView tvUserName;
    private TextView tvUserPhone;
    private ImageView ivUserAvatar;
    private ImageView ivEdit;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        userDAO = new UserDAO();
        Toolbar toolbar = findViewById(R.id.top_app_bar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        tvUserName = headerView.findViewById(R.id.tv_name);
        tvUserPhone = headerView.findViewById(R.id.tv_phone);
        ivUserAvatar = headerView.findViewById(R.id.iv_avatar);
        ivEdit = headerView.findViewById(R.id.iv_edit);

        navigationView.setNavigationItemSelectedListener(this);

        replaceFragment(getLayoutResource(), new MapsFragment());

        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION_CODE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = auth.getCurrentUser();
        if (user == null) {
            startActivity(AuthActivity.class);
        } else {
            AccountFirebaseUtil.checkEmailExists(user.getEmail(), (Boolean exists) -> {
                if (!exists) {
                    startActivity(AuthActivity.class);
                } else {
                    userDAO.getByKey(user.getUid()).observe(this, userRes -> {
                        if (userRes != null) {
                            tvUserName.setText(userRes.getFirstName() + " " + userRes.getLastName());
                            tvUserPhone.setText(userRes.getPhone());
                            Glide.with(this).load(userRes.getProfilePicture()).into(ivUserAvatar);
                        }
                    });
                }
            });
        }
    }

    private void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(this.getBaseContext(), permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            Toast.makeText(this.getBaseContext(), "Permission not granted", Toast.LENGTH_SHORT).show();
        } else {
            replaceFragment(getLayoutResource(), new MapsFragment());
            Toast.makeText(this.getBaseContext(), "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == ACCESS_FINE_LOCATION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this.getBaseContext(), "Activity Permission Granted", Toast.LENGTH_SHORT).show();
                replaceFragment(getLayoutResource(), new MapsFragment());
            } else {
                Toast.makeText(this.getBaseContext(), "Activity Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            if (mCurrentFragment != FRAGMENT_HOME) {
                replaceFragment(getLayoutResource(), new MapsFragment());
                mCurrentFragment = FRAGMENT_HOME;
            }
        } else if (id == R.id.nav_favorite) {
            if (mCurrentFragment != FRAGMENT_FAVORITE) {
                replaceFragment(getLayoutResource(), new FavoriteFragment());
                mCurrentFragment = FRAGMENT_FAVORITE;
            }
        } else if (id == R.id.nav_history) {
            if (mCurrentFragment != FRAGMENT_HISTORY) {
                replaceFragment(getLayoutResource(), new HistoryFragment());
                mCurrentFragment = FRAGMENT_HISTORY;
            }
        } else if (id == R.id.nav_logout) {
            if (mCurrentFragment != LOGOUT) {
                auth.signOut();
                startActivity(AuthActivity.class);
            }
        } else if (id == R.id.nav_add_friend) {
            if (mCurrentFragment != ACTIVITY_ADD_FRIEND) {
                startActivityNotFinish(AddFriendActivity.class);
            }
        } else if (id == R.id.nav_my_profile) {
            if (mCurrentFragment != ACTIVITY_PROFILE) {
                startActivityNotFinish(UserProfileActivity.class);
            }
        } else if (id == R.id.nav_chat) {
            if (mCurrentFragment != ACTIVITY_CHAT) {
                startActivityNotFinish(FriendsActivity.class);
            }
        } else if (id == R.id.nav_feedback) {
            if (mCurrentFragment != ACTIVITY_FEEDBACK) {
                startActivityNotFinish(FeedbackActivity.class);
            }
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
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