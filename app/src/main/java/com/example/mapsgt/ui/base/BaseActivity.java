package com.example.mapsgt.ui.base;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mapsgt.R;

public abstract  class BaseActivity extends AppCompatActivity {

    public abstract int getLayoutResource();

    public abstract void goToMainAuth();

    public abstract void goToLoginEmail();

    public abstract void goToRegister();

    public abstract void goToForgotPassword();

    public abstract void goToMainActivity();

    public void startActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        finish();
    }

    public void replaceFragment(int id_fragment, Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(id_fragment, fragment);
        fragmentTransaction.commit();
    }
}
