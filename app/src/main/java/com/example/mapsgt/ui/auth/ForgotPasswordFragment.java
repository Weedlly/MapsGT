package com.example.mapsgt.ui.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapsgt.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordFragment extends Fragment {

    private TextView emailEdt;
    private Button resetBtn;
    private TextView loginNowTV;
    private FirebaseAuth mAuth;
    private AuthActivity mAuthActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mAuthActivity = (AuthActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        emailEdt = mView.findViewById(R.id.edt_email);
        resetBtn = mView.findViewById(R.id.btn_reset);
        loginNowTV = mView.findViewById(R.id.tv_login_now);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSendEmail();
            }
        });

        loginNowTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuthActivity.goToLoginEmail();
            }
        });
        return mView;
    }

    private void handleSendEmail() {
        String emailAddress = emailEdt.getText().toString().trim();
//        TODO: handle loading

        if (TextUtils.isEmpty(emailAddress)) {
            emailEdt.setError("Enter your email");
            emailEdt.requestFocus();
            return;
        }

        // todo: check email is not exist

        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Email sent.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}