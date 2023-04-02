package com.example.mapsgt.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mapsgt.R;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginFragment extends Fragment {
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextView forgotPassTV;
    private Button loginBtn;
    private ProgressBar progressBar;
    private TextView goToRegisterLabel;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    private FirebaseAuth mAuth;

    private View mView;
    private AuthActivity mAuthActivity;

    public static LoginFragment newInstance(String strArg1) {
        LoginFragment fragment = new LoginFragment();
        Bundle bundle = new Bundle();
        bundle.putString("args", strArg1);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthActivity = (AuthActivity) getActivity();
        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_login, container, false);
        emailEditText = mView.findViewById(R.id.edt_email);
        passwordEditText = mView.findViewById(R.id.edt_password);
        loginBtn = mView.findViewById(R.id.btn_login);
        forgotPassTV = mView.findViewById(R.id.tv_forgot_password);
        goToRegisterLabel = mView.findViewById(R.id.txt_register_now);
        progressBar = mView.findViewById(R.id.progressBar);

        goToRegisterLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuthActivity.goToRegister();
            }
        });

        forgotPassTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuthActivity.goToForgotPassword();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        return mView;
    }

    private void login() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Enter email");
            emailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            emailEditText.setError("Enter password");
            emailEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        loginBtn.setVisibility(View.GONE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        loginBtn.setVisibility(View.VISIBLE);
                        if (task.isSuccessful()) {
                            mAuthActivity.goToMainActivity();
                        } else {
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}