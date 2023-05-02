package com.example.mapsgt.ui.user_profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mapsgt.R;
import com.example.mapsgt.data.entities.User;
import com.example.mapsgt.enumeration.UserGenderEnum;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST_CODE = 123;
    private static final String TAG = "EditProfileActivity";
    private ImageView avatarImg;
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
    private StorageReference mStorage;
    private String curUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        curUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        avatarImg = findViewById(R.id.iv_avatar);
        editFirstName = findViewById(R.id.edt_first_name);
        editLastName = findViewById(R.id.edt_last_name);
        editEmail = findViewById(R.id.edt_email);
        editPhoneNumber = findViewById(R.id.edt_phone);
        editDOB = findViewById(R.id.edt_dob);
        genderSpinner = findViewById(R.id.gender_spinner);
        updateBtn = findViewById(R.id.btn_update);
        cancelBtn = findViewById(R.id.btn_cancel);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);

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

        avatarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
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
                    editDOB.setText(response.getDateOfBirth());
                    gender = response.getGender();

                    String[] genders = new String[]{"Nam", "Nữ", "Khác"};

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditProfileActivity.this,
                            android.R.layout.simple_spinner_item, genders);

                    genderSpinner.setAdapter(adapter);
                    genderSpinner.setSelection(response.getGender().ordinal());

                    genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view,
                                                   int position, long id) {
                            switch (position) {
                                case 0: gender = UserGenderEnum.MALE; break;
                                case 1: gender = UserGenderEnum.FEMALE; break;
                                default: gender = UserGenderEnum.OTHER;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // TODO Auto-generated method stub
                        }
                    });

                    RequestOptions options = new RequestOptions()
                            .placeholder(R.drawable.ic_profile)
                            .error(R.drawable.google);

                    Glide.with(EditProfileActivity.this)
                            .load(response.getProfilePicture())
                            .apply(options)
                            .into(avatarImg);

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
        String emailInput = editEmail.getText().toString().trim();
        String phoneInput = editPhoneNumber.getText().toString().trim();
        String firstNameInput = editFirstName.getText().toString().trim();
        String lastNameInput = editLastName.getText().toString().trim();
        String dateOfBirthInput = editDOB.getText().toString().trim();

        String updateUrl = "/users/" + curUserId;
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(updateUrl + "/email", emailInput);
        childUpdates.put(updateUrl + "/phone", phoneInput);
        childUpdates.put(updateUrl + "/firstName", firstNameInput);
        childUpdates.put(updateUrl + "/lastName", lastNameInput);
        childUpdates.put(updateUrl + "/dateOfBirth", dateOfBirthInput);
        childUpdates.put(updateUrl + "/gender", gender);

        mDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(EditProfileActivity.this, UserProfileActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
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
        materialDatePicker.show(EditProfileActivity.this.getSupportFragmentManager(), "tag");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            uploadToFirebase(selectedImage);
        }
    }

    private void uploadToFirebase(Uri uri) {
        StorageReference fileRef = mStorage.child("avatar/" + System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference userRef = mDatabase.child("users").child(curUserId);
                        userRef.child("profilePicture").setValue(uri.toString());
                        Toast.makeText(getApplicationContext(), "Tải ảnh thành công", Toast.LENGTH_SHORT).show();
                        Glide.with(EditProfileActivity.this).load(uri).into(avatarImg);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Tải ảnh lỗi!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(cr.getType(mUri));
    }

}