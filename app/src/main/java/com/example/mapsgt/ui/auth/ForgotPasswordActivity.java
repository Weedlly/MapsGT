package com.example.mapsgt.ui.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapsgt.R;
import com.example.mapsgt.data.entities.User;
import com.example.mapsgt.database.MyDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextView emailEdt;
    private Button resetBtn;
    private TextView loginNowTV;

    private FirebaseAuth mAuth;

    private MyDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mAuth = FirebaseAuth.getInstance();
        myDatabase = MyDatabase.getInstance(this);

        emailEdt = findViewById(R.id.edt_email);
        resetBtn = findViewById(R.id.btn_reset);
        loginNowTV = findViewById(R.id.tv_login_now);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSendEmail();
            }
        });

        loginNowTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void handleSendEmail() {
        String emailAddress = emailEdt.getText().toString().trim();

        if (TextUtils.isEmpty(emailAddress)) {
            Toast.makeText(ForgotPasswordActivity.this, "Enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = myDatabase.userDAO().getUserByEmail(emailAddress);

        if(user == null) {
            Toast.makeText(ForgotPasswordActivity.this, "Email is not exist", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }
}