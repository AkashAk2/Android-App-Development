package com.example.skillswap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.skillswap.R;
import com.example.skillswap.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");

        TextView nameTextView = findViewById(R.id.nameTextView);
        TextView emailTextView = findViewById(R.id.emailTextView);
        // Initialize other TextViews

        nameTextView.setText(user.getFirstName() + " " + user.getLastName());
        emailTextView.setText(user.getEmail());
        // Set other fields

        Button addButton = findViewById(R.id.addButton);
        Button cancelButton = findViewById(R.id.cancelButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current user
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                // Check if user is logged in
                if (currentUser != null && !currentUser.isAnonymous()) {
                    // User is logged in, allow them to add a connection

                    // Get current user's ID
                    String currentUserId = currentUser.getUid();

                    // Get reference to current user's connections
                    DatabaseReference connectionsRef = FirebaseDatabase.getInstance()
                            .getReference("connections")
                            .child(currentUserId);

                    // Sanitize the email
                    String sanitizedEmail = user.getEmail().replace('.', ',');

                    String originalEmail = sanitizedEmail.replace(',', '.');

                    // Add user to current user's connections using user's uid
                    connectionsRef.child(user.getUid()).setValue(true);

                    Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    // User is not logged in, redirect them to ProfileActivity

                    Intent intent = new Intent(UserProfileActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}