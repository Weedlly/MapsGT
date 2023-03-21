package com.example.mapsgt.ui.auth;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapsgt.MainActivity;
import com.example.mapsgt.R;
import com.example.mapsgt.data.entities.User;
import com.example.mapsgt.database.MyDatabase;
import com.example.mapsgt.enumeration.UserGenderEnum;
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

public class RegisterActivity extends AppCompatActivity {

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
    private MyDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        myDatabase = MyDatabase.getInstance(this);

        editFirstName = findViewById(R.id.edt_first_name);
        editLastName = findViewById(R.id.edt_last_name);
        editTextEmail = findViewById(R.id.edt_email);
        editPhoneNumber = findViewById(R.id.edt_phone);
        editDOB = findViewById(R.id.edt_dob);
        editTextPassword = findViewById(R.id.edt_password);
        genderSpinner = findViewById(R.id.gender_spinner);
        btnReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        goToLoginTV = findViewById(R.id.tv_login_now);

        String[] genders = new String[]{UserGenderEnum.MALE.name(), UserGenderEnum.FEMALE.name(), UserGenderEnum.OTHER.name()};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
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

        mAuth = FirebaseAuth.getInstance();

        goToLoginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
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
    }

    private void signUp() {
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String dob = editDOB.getText().toString();
        String email = editTextEmail.getText().toString().trim();
        String phoneNo = editPhoneNumber.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(RegisterActivity.this, "Enter first name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(RegisterActivity.this, "Enter last name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(dob)) {
            Toast.makeText(RegisterActivity.this, "Select date of birth", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phoneNo)) {
            Toast.makeText(RegisterActivity.this, "Enter phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
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

                            User user = new User(authUser.getUid(), authUser.getEmail(), phoneNo, firstName, lastName, convertStringToDate(dob), gender);
                            myDatabase.userDAO().insertUser(user);

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }
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
        materialDatePicker.show(getSupportFragmentManager(), "tag");
    }

    private Date convertStringToDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date = dateFormat.parse(dateStr);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}