package com.example.mapsgt.data.entities;

import android.text.TextUtils;
import android.util.Patterns;

import com.example.mapsgt.enumeration.UserGenderEnum;

public class User {
    private String id;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String profilePicture;
    private UserGenderEnum gender;
    private double latitude;
    private double longitude;
    private boolean isSharing;

    public User() {
    }

    public User(String id, String email, String phone, String firstName, String lastName, String dateOfBirth, UserGenderEnum gender, double latitude, double longitude, boolean isSharing, String profilePicture) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isSharing = isSharing;
        this.profilePicture = profilePicture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean getIsSharing() {
        return isSharing;
    }

    public void setIsSharing(boolean isSharing) {
        this.isSharing = isSharing;
    }
}
