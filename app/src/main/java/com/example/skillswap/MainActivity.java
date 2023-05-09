package com.example.skillswap;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView profileImageView = findViewById(R.id.profileImageView);
        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
        SearchView searchBar = findViewById(R.id.searchBar);
        RecyclerView skillRecommendationsRecyclerView = findViewById(R.id.skillRecommendationsRecyclerView);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            welcomeTextView.setText("Hello, " + user.getFirstName() + "!");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            // Set an onClickListener for the profileImageView to open the Profile screen
            profileImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.putExtra("googleSignInClientId", getString(R.string.default_web_client_id));
                    startActivity(intent);
                }
            });

            //TODO Implement search functionality, personalized skill recommendations and bottom navigation bar logic here

        } else {
            welcomeTextView.setText("Hello, guest user!");
            profileImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Replace ProfileActivity with the actual Profile activity class
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_menu_item:
                Intent homeIntent = new Intent(MainActivity.this, MainActivity.class);
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
                Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
                return true;

            default:
                return false;
        }
    }
}
