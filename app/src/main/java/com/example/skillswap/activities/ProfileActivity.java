package com.example.skillswap.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.skillswap.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        String googleSignInClientId = getIntent().getStringExtra("googleSignInClientId");

        if (googleSignInClientId != null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(googleSignInClientId)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        }

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.profile_menu_item);
        }
    }
}