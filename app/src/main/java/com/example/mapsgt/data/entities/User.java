package com.example.mapsgt.data.entities;

import android.text.TextUtils;
import android.util.Patterns;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.mapsgt.enumeration.UserGenderEnum;

import java.util.Date;

@Entity(tableName = "user", indices = {@Index(value = {"email", "username"},
        unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    private int id;
    private String username;
    private String email;
    private String phone;
    private String password;
    @ColumnInfo(name = "first_name")
    private String firstName;
    @ColumnInfo(name = "last_name")
    private String lastName;
    @ColumnInfo(name = "date_of_birth")
    private Date dateOfBirth;
    @ColumnInfo(name = "profile_picture")
    private String profilePicture;
    private UserGenderEnum gender;
    @ColumnInfo(name = "last_known_location_id")
    private int lastKnownLocationId;

    public User(String username, String email, String phone, String password, String firstName, String lastName, Date dateOfBirth, String profilePicture, UserGenderEnum gender) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.profilePicture = profilePicture;
        this.gender = gender;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLastKnownLocationId(int lastKnownLocationId) {
        this.lastKnownLocationId = lastKnownLocationId;
    }

    public int getLastKnownLocationId() {
        return lastKnownLocationId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public UserGenderEnum getGender() {
        return gender;
    }

    public void setGender(UserGenderEnum gender) {
        this.gender = gender;
    }

    public boolean isValidEmail() {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", profilePicture='" + profilePicture + '\'' +
                ", gender=" + gender +
                ", lastKnownLocationId=" + lastKnownLocationId +
                '}';
    }
}
