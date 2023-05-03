package com.example.mapsgt.ui.user_profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mapsgt.MainActivity;
import com.example.mapsgt.R;
import com.example.mapsgt.data.entities.User;
import com.example.mapsgt.enumeration.UserGenderEnum;
import com.example.mapsgt.ui.base.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends BaseActivity {
    private ImageView avatarImg;
    private TextView nameTv;
    private TextView friendNumberTv;
    private TextView emailTv;
    private TextView phoneNumberTv;
    private TextView dobTv;
    private TextView genderTv;
    private Button editBtn;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Trang cá nhân");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        avatarImg = findViewById(R.id.iv_avatar);
        nameTv = findViewById(R.id.tv_name);
        friendNumberTv = findViewById(R.id.tv_friend_number);
        emailTv = findViewById(R.id.tv_email);
        phoneNumberTv = findViewById(R.id.tv_phone_number);
        dobTv = findViewById(R.id.tv_dob);
        genderTv = findViewById(R.id.tv_gender);
        editBtn = findViewById(R.id.btn_edit_profile);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
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
                    String personProfileImage = response.getProfilePicture();

                    RequestOptions options = new RequestOptions()
                            .placeholder(R.drawable.ic_profile)
                            .error(R.drawable.google);

                    Glide.with(getApplicationContext())
                            .load(personProfileImage)
                            .apply(options)
                            .into(avatarImg);

                    nameTv.setText(response.getFirstName() + " " + response.getLastName());

                    emailTv.setText(response.getEmail());
                    phoneNumberTv.setText(response.getPhone());
                    dobTv.setText(response.getDateOfBirth());
                    genderTv.setText(displayGenderText(response.getGender()));
                    DataSnapshot friendsRef = snapshot.child("users").child(currentUserId).child("friends");
                    friendNumberTv.setText(String.valueOf(friendsRef.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String displayGenderText(UserGenderEnum genderEnum) {
        switch (genderEnum) {
            case MALE: return "Nam";
            case FEMALE: return "Nữ";
            default: return "Không xác định";
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(MainActivity.class);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getLayoutResource() {
        return 0;
    }
}