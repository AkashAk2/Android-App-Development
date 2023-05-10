package com.example.skillswap.models;

public class User {
    private String firstName;
    private String lastName;

    private String dob;
    private String mobileNumber;
    private String emailID;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String firstName, String lastName, String dob, String mobileNumber, String emailID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.mobileNumber = mobileNumber;
        this.emailID = emailID;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return emailID;
    }

    public void setEmail(String emailID) {
        this.emailID = emailID;
    }
}
