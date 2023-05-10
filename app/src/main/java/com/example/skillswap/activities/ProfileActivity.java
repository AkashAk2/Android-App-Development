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

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
                updateUserProfile();
                toggleEdit(false);
                editProfileButton.setVisibility(View.VISIBLE);
                saveProfileButton.setVisibility(View.GONE);
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

                // Sign out from Google (if using Google Sign In)
                if (mGoogleSignInClient != null) {
                    mGoogleSignInClient.signOut();
                }

                // Redirect the user to the Login/Register/Guest screen
                Intent intent = new Intent(ProfileActivity.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        });

        // Display the user's email
        FirebaseUser currentUser = mAuth.getCurrentUser();
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

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.profile_menu_item);
        }
    }
}