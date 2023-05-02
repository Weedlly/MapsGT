package com.example.mapsgt.ui.auth;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapsgt.R;
import com.example.mapsgt.data.entities.User;
import com.example.mapsgt.enumeration.UserGenderEnum;
import com.example.mapsgt.network.FirebaseApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private TextInputEditText editFirstName;
    private TextInputEditText editLastName;
    private TextInputEditText editDOB;
    private TextInputEditText editTextEmail;
    private TextInputEditText editPhoneNumber;
    private TextInputEditText editTextPassword;
    private Spinner genderSpinner;

    private UserGenderEnum gender;

    private Button btnReg;
    private ProgressBar progressBar;

    private TextView goToLoginTV;

    private FirebaseAuth mAuth;
    private AuthActivity mAuthActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthActivity = (AuthActivity) getActivity();
        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_register, container, false);
        editFirstName = mView.findViewById(R.id.edt_first_name);
        editLastName = mView.findViewById(R.id.edt_last_name);
        editTextEmail = mView.findViewById(R.id.edt_email);
        editPhoneNumber = mView.findViewById(R.id.edt_phone);
        editDOB = mView.findViewById(R.id.edt_dob);
        editTextPassword = mView.findViewById(R.id.edt_password);
        genderSpinner = mView.findViewById(R.id.gender_spinner);
        btnReg = mView.findViewById(R.id.btn_register);
        progressBar = mView.findViewById(R.id.progressBar);
        goToLoginTV = mView.findViewById(R.id.tv_login_now);

        String[] genders = new String[]{UserGenderEnum.MALE.name(), UserGenderEnum.FEMALE.name(), UserGenderEnum.OTHER.name()};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, genders);

        genderSpinner.setAdapter(adapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                gender = UserGenderEnum.valueOf((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        goToLoginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuthActivity.goToLoginEmail();
            }
        });

        editDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSelectDate();
            }
        });

        editDOB.setShowSoftInputOnFocus(false);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
        return mView;
    }

    private void signUp() {
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String dob = editDOB.getText().toString();
        String email = editTextEmail.getText().toString().trim();
        String phoneNo = editPhoneNumber.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
            editFirstName.setError("Enter first name");
            editFirstName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            editLastName.setError("Enter last name");
            editLastName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(dob)) {
            editDOB.setError("Select date of birth");
            editDOB.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            //TODO: check email valid
            editTextEmail.setError("Enter email");
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phoneNo)) {
//            TODO: validate phone is existed
            editPhoneNumber.setError("Enter phone number");
            editPhoneNumber.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
//            TODO: validate the length of password
            editTextPassword.setError("Enter password");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnReg.setVisibility(View.GONE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        btnReg.setVisibility(View.VISIBLE);
                        if (task.isSuccessful()) {
                            FirebaseUser authUser = mAuth.getCurrentUser();

                            if (authUser != null) {
                                User user = new User(authUser.getUid(), authUser.getEmail(), phoneNo, firstName, lastName, dob, gender, -1, -1, false, "https://raw.githubusercontent.com/gotitinc/aha-assets/master/uifaces/m-10.jpg");
                                createUserOnFirebase(user);
                            }
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createUserOnFirebase(User user) {
        Call<User> call = FirebaseApiClient.getFirebaseApi().createUser(user.getId(), user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User createdUser = response.body();
                    // Process the created user data here
                    Log.e("User", createdUser.toString());
                    mAuthActivity.goToMainActivity();
                } else {
                    // Handle error here
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Handle error here
            }
        });
    }

    private void handleSelectDate() {
        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection));
                editDOB.setText(date);
            }
        });
        materialDatePicker.show(getActivity().getSupportFragmentManager(), "tag");
    }
}