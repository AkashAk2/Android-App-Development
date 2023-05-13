package com.example.skillswap.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.skillswap.R;
import com.example.skillswap.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference mDatabase;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText mobileNumberEditText;
    private EditText dobEditText;
    private ValueEventListener userDetailsValueEventListener;
    private TextView guestTitle;
    private TextView guestText;
    private Button loginButton;
    private Button signupButton;
    private Button logoutButton;
    private TextView userEmail;
    private Button editprofileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String googleSignInClientId = getIntent().getStringExtra("googleSignInClientId");

        if (googleSignInClientId != null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(googleSignInClientId)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        }

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        mobileNumberEditText = findViewById(R.id.mobileNumberEditText);
        dobEditText = findViewById(R.id.dobEditText);
        guestTitle = findViewById(R.id.guestTitle);
        guestText = findViewById(R.id.guestText);
        loginButton = findViewById(R.id.loginButton);
        logoutButton = findViewById(R.id.logoutButton);
        signupButton = findViewById(R.id.signupButton);
        userEmail = findViewById(R.id.userEmailTextView);
        editprofileButton = findViewById(R.id.editProfileButton);

        // Load user details
        loadUserDetails();

        // Set up the DatePickerDialog for the dobEditText
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener datePickerDialog =
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDob(calendar);
                    }
                };

        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ProfileActivity.this, datePickerDialog,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Add the Edit and Save button functionality
        final Button editProfileButton = findViewById(R.id.editProfileButton);
        final Button saveProfileButton = findViewById(R.id.saveProfileButton);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEdit(true);
                editProfileButton.setVisibility(View.GONE);
                saveProfileButton.setVisibility(View.VISIBLE);
            }
        });

        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    updateUserProfile();
                    toggleEdit(false);
                    editProfileButton.setVisibility(View.VISIBLE);
                    saveProfileButton.setVisibility(View.GONE);
                }
            }
        });

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove ValueEventListener
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null && userDetailsValueEventListener != null) {
                    String userId = currentUser.getUid();
                    DatabaseReference userRef = mDatabase.child("users").child(userId);
                    userRef.removeEventListener(userDetailsValueEventListener);
                }

                // Sign out from Firebase
                mAuth.signOut();
                updateUI(null);

                // Sign out from Google (if using Google Sign In)
                if (mGoogleSignInClient != null) {
                    mGoogleSignInClient.signOut();
                    // Update the UI
                    updateUI(null);
                }

                // Redirect the user to the Login/Register/Guest screen
                Intent intent = new Intent(ProfileActivity.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Login activity
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
//                finish();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Signup activity
                Intent intent = new Intent(ProfileActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        // Display the user's email
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        if (currentUser != null) {
            TextView userEmailTextView = findViewById(R.id.userEmailTextView);
            userEmailTextView.setText(currentUser.getEmail());
        } else {
            GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
            if (googleSignInAccount != null) {
                TextView userEmailTextView = findViewById(R.id.userEmailTextView);
                userEmailTextView.setText(googleSignInAccount.getEmail());
            }
        }
    }

    private boolean validateFields() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String mobileNumber = mobileNumberEditText.getText().toString().trim();
        String dob = dobEditText.getText().toString().trim();

        if (firstName.isEmpty()) {
            firstNameEditText.setError("Please enter your first name");
            return false;
        }

        if (!isValidName(firstName)) {
            firstNameEditText.setError("First name should not contain special characters");
            return false;
        }

        if (lastName.isEmpty()) {
            lastNameEditText.setError("Please enter your last name");
            return false;
        }

        if (!isValidName(lastName)) {
            lastNameEditText.setError("Last name should not contain numeric or special characters");
            return false;
        }

        if (mobileNumber.isEmpty()) {
            mobileNumberEditText.setError("Please enter your mobile number");
            return false;
        }

        if (!isValidAustralianPhoneNumber(mobileNumber)) {
            mobileNumberEditText.setError("Please enter a valid Australian phone number");
            return false;
        }

        if (dob.isEmpty()) {
            dobEditText.setError("Please select your date of birth");
            return false;
        }

        if (!isValidDateOfBirth(dob)) {
            dobEditText.setError("Please enter a valid date of birth");
            return false;
        }

        return true;
    }

    private boolean isValidName(String name) {
        // Regular expression to allow only alphabets and spaces
        String regex = "^[a-zA-Z0-9\\s]+$";
        return name.matches(regex);
    }

    private boolean isValidAustralianPhoneNumber(String phoneNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber auNumber = phoneNumberUtil.parse(phoneNumber, "AU");
            return phoneNumberUtil.isValidNumberForRegion(auNumber, "AU");
        } catch (NumberParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValidDateOfBirth(String dob) {
        // Perform your custom validation logic for date of birth
        // Example: Check if the date is not in the future
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        Date currentDate = new Date();
        Date dateOfBirth;
        try {
            dateOfBirth = dateFormat.parse(dob);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return dateOfBirth.before(currentDate);
    }


    private void toggleEdit(boolean editable) {
        firstNameEditText.setEnabled(editable);
        lastNameEditText.setEnabled(editable);
        mobileNumberEditText.setEnabled(editable);
        dobEditText.setEnabled(editable);
    }

    private void updateDob(Calendar calendar) {
        String format = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        dobEditText.setText(sdf.format(calendar.getTime()));
    }

    private void loadUserDetails() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = mDatabase.child("users").child(userId);

            userDetailsValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            firstNameEditText.setText(user.getFirstName());
                            lastNameEditText.setText(user.getLastName());
                            mobileNumberEditText.setText(user.getMobileNumber());
                            dobEditText.setText(user.getDob());
                        }
                    } else {
                        showToast("User not found. Please update your profile.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    showToast("Failed to load user data. Please try again.");
                }
            };
            userRef.addValueEventListener(userDetailsValueEventListener);
        }
    }


    private void updateUserProfile() {
        // Save the updated user profile to Firebase
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String mobileNumber = mobileNumberEditText.getText().toString().trim();
        String dob = dobEditText.getText().toString().trim();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            User updatedUser = new User(firstName, lastName, dob, mobileNumber, currentUser.getEmail());

            mDatabase.child("users").child(userId).setValue(updatedUser)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Update successful
                                showToast("Profile updated successfully!");
                            } else {
                                // Update failed
                                showToast("Failed to update profile. Please try again.");
                            }
                        }
                    });
        }
    }

    private void showToast(String message) {
        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
            guestTitle.setVisibility(View.VISIBLE);
            guestText.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            signupButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            userEmail.setVisibility(View.GONE);
            firstNameEditText.setVisibility(View.GONE);
            lastNameEditText.setVisibility(View.GONE);
            mobileNumberEditText.setVisibility(View.GONE);
            dobEditText.setVisibility(View.GONE);
            editprofileButton.setVisibility(View.GONE);


        } else {
            guestTitle.setVisibility(View.GONE);
            guestText.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            signupButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            userEmail.setVisibility(View.VISIBLE);
            firstNameEditText.setVisibility(View.VISIBLE);
            lastNameEditText.setVisibility(View.VISIBLE);
            mobileNumberEditText.setVisibility(View.VISIBLE);
            dobEditText.setVisibility(View.VISIBLE);
            editprofileButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.profile_menu_item);
        }
    }
}