package com.example.skillswap.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skillswap.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is signed in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // If user is not null and is not a guest user, redirect to MainActivity
        if (currentUser != null) {
            startActivity(new Intent(HomePage.this, MainActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_home_page);

        //Find buttons by their ids
        Button loginButton = findViewById(R.id.loginButton);
        Button signupButton = findViewById(R.id.signupButton);
        Button guestButton = findViewById(R.id.guestButton);

        //Setting click listeners for the buttons
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Login activity
                Intent intent = new Intent(HomePage.this, LoginActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
//                finish();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Signup activity
                Intent intent = new Intent(HomePage.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sign out from Firebase (if there is a currently signed-in user)
                FirebaseAuth.getInstance().signOut();

                // Proceed with anonymous sign-in
                FirebaseAuth.getInstance().signInAnonymously()
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign-in success, update UI with the signed-in user's information
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    // Proceed to the MainActivity or any other desired activity
                                    Intent intent = new Intent(HomePage.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign-in fails, display a message to the user.
                                    Log.w("GuestButtonAnonymous", "signInAnonymously:failure", task.getException());
                                    Toast.makeText(HomePage.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    // Handle any additional error handling if required
                                    // ...
                                }
                            }
                        });
            }
        });


    }
}