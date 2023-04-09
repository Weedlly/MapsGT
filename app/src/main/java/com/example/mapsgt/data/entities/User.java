package com.example.mapsgt.data.entities;

import android.text.TextUtils;
import android.util.Patterns;

import com.example.mapsgt.enumeration.UserGenderEnum;

import java.util.Date;

public class User {
    private String id;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String profilePicture;
    private UserGenderEnum gender;
    private int lastKnownLocationId;

    public User() { //Todo
        // Required public no-argument constructor
    }

    public User(String id, String email, String phone, String firstName, String lastName, Date dateOfBirth, UserGenderEnum gender) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLastKnownLocationId(int lastKnownLocationId) {
        this.lastKnownLocationId = lastKnownLocationId;
    }

    public int getLastKnownLocationId() {
        return lastKnownLocationId;
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
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", profilePicture='" + profilePicture + '\'' +
                ", gender=" + gender +
                ", lastKnownLocationId=" + lastKnownLocationId +
                '}';
    }
}
