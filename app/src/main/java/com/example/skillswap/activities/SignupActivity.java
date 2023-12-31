package com.example.skillswap.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skillswap.R;
import com.example.skillswap.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    // Move these EditText declarations outside of onCreate
    private EditText firstName;
    private EditText lastName;
    private EditText mobileNumber;
    private EditText emailID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        // Initialize the EditText variables
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        mobileNumber = findViewById(R.id.mobileNumber);
        emailID = findViewById(R.id.emailEditText);
        EditText password = findViewById(R.id.passwordEditText);
        EditText reEnteredPassword = findViewById(R.id.passwordReEnterEditText);
        Button registerButton = findViewById(R.id.register);
        TextView signupTitle = findViewById(R.id.signupTitle);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if all the fields are filled
                if (firstName.getText().toString().trim().isEmpty() ||
                        lastName.getText().toString().trim().isEmpty() ||
                        mobileNumber.getText().toString().trim().isEmpty() ||
                        emailID.getText().toString().trim().isEmpty() ||
                        password.getText().toString().trim().isEmpty() ||
                        reEnteredPassword.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show();
                } else {
                    String email = emailID.getText().toString().trim();
                    String pass = password.getText().toString().trim();
                    String reEnteredPass = reEnteredPassword.getText().toString().trim();
                    String mobile = mobileNumber.getText().toString().trim();

                    if (!isValidMobileNumber(mobile)) {
                        Toast.makeText(SignupActivity.this, "Mobile number must be at least 10 characters long.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!isValidPassword(pass)) {
                        Toast.makeText(SignupActivity.this, "Password must be at least 8 characters long and contain at least 1 symbol.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (pass.equals(reEnteredPass)) {
                        registerUser(email, pass);
                    } else {
                        // Show a message to the user if passwords don't match
                        Toast.makeText(SignupActivity.this, "Passwords don't match.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void registerUser(String email, String password) {
        Log.d("SignupActivity", "registerUser: Attempting to register user with email: " + email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("SignupActivity", "registerUser: User registration successful");

                            // Registration successful, store user data in Realtime Database
                            FirebaseUser user = mAuth.getCurrentUser();
                            storeUserData(user.getUid());
                            Toast.makeText(SignupActivity.this, "You are successfully registered!.", Toast.LENGTH_SHORT).show();

                            // Navigate to the homepage activity
                            Intent intent = new Intent(SignupActivity.this, HomePage.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Registration failed, display a message to the user
                            Log.e("SignupActivity", "registerUser: User registration failed", task.getException());
                            Toast.makeText(SignupActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false;
        }

        boolean hasSymbol = false;
        for (char c : password.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                hasSymbol = true;
                break;
            }
        }

        return hasSymbol;
    }

    private boolean isValidMobileNumber(String mobileNumber) {
        return mobileNumber.length() >= 10;
    }



    private void storeUserData(String uid) {
        Log.d("SignupActivity", "storeUserData: Storing user data for UID: " + uid);
        User newUser = new User(
                uid, // Add this line to pass the user's Firebase UID
                firstName.getText().toString().trim(),
                lastName.getText().toString().trim(),
                "", // Google doesn't provide separate first and last names, so leave lastName empty
                mobileNumber.getText().toString().trim(),
                emailID.getText().toString().trim()
        );

        usersRef.child(uid).setValue(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("SignupActivity", "storeUserData: User data stored successfully");
                        Toast.makeText(SignupActivity.this, "User data stored successfully.", Toast.LENGTH_SHORT).show();

                        // Navigate to the homepage activity
                        Intent intent = new Intent(SignupActivity.this, HomePage.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("SignupActivity", "storeUserData: Error storing user data", e);
                        Toast.makeText(SignupActivity.this, "Error storing user data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
