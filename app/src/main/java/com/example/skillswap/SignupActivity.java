package com.example.skillswap;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailID.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String reEnteredPass = reEnteredPassword.getText().toString().trim();

                if (pass.equals(reEnteredPass)) {
                    registerUser(email, pass);
                } else {
                    // Show a message to the user if passwords don't match
                    Toast.makeText(SignupActivity.this, "Passwords don't match.", Toast.LENGTH_SHORT).show();
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

    private void storeUserData(String uid) {
        Log.d("SignupActivity", "storeUserData: Storing user data for UID: " + uid);
        User newUser = new User(
                firstName.getText().toString().trim(),
                lastName.getText().toString().trim(),
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
