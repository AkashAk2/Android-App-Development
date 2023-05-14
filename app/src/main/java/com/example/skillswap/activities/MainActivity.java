package com.example.skillswap.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.skillswap.R;
import com.example.skillswap.UpdateSkillsWorker;
import com.example.skillswap.adapters.PeopleAdapter;
import com.example.skillswap.adapters.UserAdapter;
import com.example.skillswap.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.TimeUnit;


public class MainActivity extends BaseActivity {
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference databaseReference;

    private List<User> userList = new ArrayList<>();
    private UserAdapter userAdapter;
    private PeopleAdapter peopleAdapter;
    private List<String> peopleList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        peopleAdapter = new PeopleAdapter(peopleList, MainActivity.this, currentUser);


        ImageView profileImageView = findViewById(R.id.profileImageView);
        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
        SearchView searchBar = findViewById(R.id.searchBar);
        RecyclerView skillRecommendationsRecyclerView = findViewById(R.id.skillRecommendationsRecyclerView);
        userAdapter = new UserAdapter(userList, MainActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        skillRecommendationsRecyclerView.setLayoutManager(layoutManager);
        skillRecommendationsRecyclerView.setAdapter(userAdapter);
        RecyclerView peopleRecyclerView = findViewById(R.id.peopleRecyclerView);
        peopleAdapter = new PeopleAdapter(peopleList, MainActivity.this, currentUser);
        RecyclerView.LayoutManager peopleLayoutManager = new LinearLayoutManager(MainActivity.this);
        peopleRecyclerView.setLayoutManager(peopleLayoutManager);
        peopleRecyclerView.setAdapter(peopleAdapter);

        Button startWorkButton = findViewById(R.id.startWorkButton);


        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null && !currentUser.isAnonymous()) {
                            welcomeTextView.setText("Hello, " + user.getFirstName() + "!");

                            // Get reference to current user's connections
                            DatabaseReference connectionsRef = FirebaseDatabase.getInstance()
                                    .getReference("connections")
                                    .child(currentUser.getUid());


                            // Retrieve current user's connections
                            connectionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        String connectedUserId = snapshot.getKey();

                                        // Retrieve connected user's details

                                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(connectedUserId);
                                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                User connectedUser = dataSnapshot.getValue(User.class);
                                                peopleAdapter.addUser(connectedUserId);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                // Handle possible errors.
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle possible errors.
                                }
                            });
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
            searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (!newText.isEmpty()) {
                        searchUsers(newText);
                    } else {
                        userList.clear();
                        userAdapter.notifyDataSetChanged();
                    }
                    return false;
                }
            });


        }
        else {
            welcomeTextView.setText("Hello, Guest user!");

            // User is not signed in, proceed with anonymous sign-in
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign-in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                // Proceed with retrieving data and setting up the UI
                                // ...
                            } else {
                                // If sign-in fails, display a message to the user.
                                Log.w("GuestAnonymous", "signInAnonymously:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                // Handle any additional error handling if required
                                // ...
                            }
                        }
                    });
            profileImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Replace ProfileActivity with the actual Profile activity class
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
            });
        }

        if(currentUser != null && currentUser.isAnonymous()) {
            welcomeTextView.setText("Hello, Guest user!");
        }

        // Set an OnClickListener...
        startWorkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new PeriodicWorkRequest...
                PeriodicWorkRequest updateSkillsRequest =
                        new PeriodicWorkRequest.Builder(UpdateSkillsWorker.class, 24, TimeUnit.HOURS)
                                .build();

                // Enqueue the work...
                WorkManager.getInstance(MainActivity.this).enqueue(updateSkillsRequest);
            }
        });
    }

    private void searchUsers(String skill) {
        skill = skill.toLowerCase(); // convert skill to lowercase for case-insensitive search
        Log.d("SearchUsers", "Searching for skill: " + skill); // Log the skill being searched for
        DatabaseReference userskills = FirebaseDatabase.getInstance().getReference("userskills").child(skill);
        userskills.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("SearchUsers", "Received data from Firebase: " + dataSnapshot.toString()); // Log the received data
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uid = snapshot.getKey();
                    Log.d("SearchUsers", "Processing user with ID: " + uid); // Log the user ID being processed
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            User user = userSnapshot.getValue(User.class);
                            if (user != null) {
                                Log.d("SearchUsers", "Found user: " + user.toString()); // Log the found user
                                userList.add(user);
                            } else {
                                Log.d("SearchUsers", "User object is null"); // Log if the User object is null
                            }
                            userAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("SearchUsers", "Firebase error: " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("SearchUsers", "Firebase error: " + databaseError.getMessage());
            }
        });
    }




    @Override
    public void onBackPressed() {
        // Minimize the app when the back button is pressed on the main activity
        moveTaskToBack(true);
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.home_menu_item);
        }
    }
}
