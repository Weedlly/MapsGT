package com.example.mapsgt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.mapsgt.ui.auth.LoginActivity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private TextView txtView;
    private Button logoutBtn;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        logoutBtn = findViewById(R.id.btn_logout);
        txtView = findViewById(R.id.txt_view);
        user = auth.getCurrentUser();

        if(user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            txtView.setText(user.getEmail());
        }

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}