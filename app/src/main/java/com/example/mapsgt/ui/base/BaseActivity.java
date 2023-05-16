package com.example.mapsgt.ui.base;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public abstract class BaseActivity extends AppCompatActivity {

    public abstract int getLayoutResource();
    public void startActivityNotFinish(Class<?> activityClass) {
        Intent intent = new Intent(getApplicationContext(), activityClass);
        startActivity(intent);
    }
    public void startActivity(Class<?> activityClass) {
        Intent intent = new Intent(getApplicationContext(), activityClass);
        startActivity(intent);
        finish();
    }
    public void startActivity(Class<?> activityClass, Bundle bundle) {
        Intent intent = new Intent(getApplicationContext(), activityClass);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
    public void replaceFragment(int id_fragment, Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(id_fragment, fragment);
        fragmentTransaction.commit();
    }
}
