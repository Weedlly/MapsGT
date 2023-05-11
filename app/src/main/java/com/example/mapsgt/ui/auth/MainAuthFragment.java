package com.example.mapsgt.ui.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mapsgt.R;

public class MainAuthFragment extends Fragment {
    public static final int RC_SIGN_IN = 1;
    private Button ggSignInBtn;
    private Button emailSignInBtn;
    private AuthActivity mAuthActivity;

    public MainAuthFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthActivity = (AuthActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_main_auth, container, false);
        ggSignInBtn = mView.findViewById(R.id.btn_sign_in_google);
        emailSignInBtn = mView.findViewById(R.id.btn_sign_in_email);

        ggSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuthActivity.signIn();
            }
        });

        emailSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuthActivity.goToLoginEmail();
            }
        });
        return mView;
    }
}