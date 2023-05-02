package com.example.mapsgt.ui.user_profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.mapsgt.R;
import com.example.mapsgt.data.entities.User;
import com.example.mapsgt.enumeration.UserGenderEnum;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {
    private TextInputEditText editFirstName;
    private TextInputEditText editLastName;
    private TextInputEditText editDOB;
    private TextInputEditText editEmail;
    private TextInputEditText editPhoneNumber;
    private Spinner genderSpinner;

    private UserGenderEnum gender;

    private Button updateBtn;
    private Button cancelBtn;
    private ProgressBar progressBar;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        editFirstName = findViewById(R.id.edt_first_name);
        editLastName = findViewById(R.id.edt_last_name);
        editEmail = findViewById(R.id.edt_email);
        editPhoneNumber = findViewById(R.id.edt_phone);
        editDOB = findViewById(R.id.edt_dob);
        genderSpinner = findViewById(R.id.gender_spinner);
        updateBtn = findViewById(R.id.btn_update);
        cancelBtn = findViewById(R.id.btn_cancel);
        progressBar = findViewById(R.id.progressBar);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               updateProfile();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });

        getUserInfo();
    }

    private void getUserInfo() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User response = snapshot.child("users").child(currentUserId).getValue(User.class);
                if (response != null) {
                    editFirstName.setText(response.getFirstName());
                    editLastName.setText(response.getLastName());
                    editEmail.setText(response.getEmail());
                    editPhoneNumber.setText(response.getPhone());
                    editDOB.setText(formatOutputDate(response.getDateOfBirth()));

                    String[] genders = new String[]{UserGenderEnum.MALE.name(), UserGenderEnum.FEMALE.name(), UserGenderEnum.OTHER.name()};

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditProfileActivity.this,
                            android.R.layout.simple_spinner_item, genders);

                    genderSpinner.setAdapter(adapter);
                    genderSpinner.setSelection(response.getGender().ordinal());

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

                    editDOB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            handleSelectDate();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateProfile() {

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
        materialDatePicker.show(EditProfileActivity.this.getSupportFragmentManager(), "tag");
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

    private String formatOutputDate(String dateString) {
        DateFormat inputFormat = new SimpleDateFormat("MMM d, yyyy hh:mm:ss a", Locale.US);
        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        String outputDateString = null;

        try {
            Date date = inputFormat.parse(dateString);
            outputDateString = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return outputDateString;
    }

}