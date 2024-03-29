package com.example.mapsgt.ui.user_profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mapsgt.MainActivity;
import com.example.mapsgt.R;
import com.example.mapsgt.data.dao.FriendRelationshipDAO;
import com.example.mapsgt.data.dao.UserDAO;
import com.example.mapsgt.enumeration.UserGenderEnum;
import com.example.mapsgt.ui.base.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;

public class UserProfileActivity extends BaseActivity {
    private ImageView avatarImg;
    private TextView nameTv;
    private TextView friendNumberTv;
    private TextView emailTv;
    private TextView phoneNumberTv;
    private TextView dobTv;
    private TextView genderTv;
    private Button editBtn;
    private UserDAO userDAO;
    private FriendRelationshipDAO friendRelationshipDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Trang cá nhân");

        userDAO = new UserDAO();
        friendRelationshipDAO = new FriendRelationshipDAO();

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

        userDAO.getByKey(currentUserId).observe(this, user -> {
            String personProfileImage = user.getProfilePicture();

            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.google);

            Glide.with(getApplicationContext())
                    .load(personProfileImage)
                    .apply(options)
                    .into(avatarImg);

            nameTv.setText(user.getFirstName() + " " + user.getLastName());

            emailTv.setText(user.getEmail());
            phoneNumberTv.setText(user.getPhone());
            dobTv.setText(user.getDateOfBirth());
            genderTv.setText(displayGenderText(user.getGender()));
        });

        friendRelationshipDAO.getFriendList(currentUserId).observe(this, friendList -> {
            friendNumberTv.setText(String.valueOf(friendList.size()));
        });
    }

    private String displayGenderText(UserGenderEnum genderEnum) {
        switch (genderEnum) {
            case MALE:
                return "Nam";
            case FEMALE:
                return "Nữ";
            default:
                return "Không xác định";
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