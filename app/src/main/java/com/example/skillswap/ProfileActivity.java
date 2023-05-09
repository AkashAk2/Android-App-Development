package com.example.skillswap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
//        bottomNavigationView.setSelectedItemId(R.id.profile_menu_item);

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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_menu_item:
                Intent homeIntent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(homeIntent);
                return true;

            case R.id.chat_menu_item:
                // Handle the chat menu item click
                Toast.makeText(this, "Chat clicked", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.myskill_menu_item:
                // Handle the my skill menu item click
                Toast.makeText(this, "My Skill clicked", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.schedule_menu_item:
                // Handle the schedule menu item click
                Toast.makeText(this, "Schedule clicked", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.profile_menu_item:
                Intent profileIntent = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
                return true;

            default:
                return false;
        }
    }
}