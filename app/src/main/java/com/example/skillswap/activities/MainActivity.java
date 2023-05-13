package com.example.skillswap.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillswap.R;
import com.example.skillswap.adapters.PeopleAdapter;
import com.example.skillswap.adapters.UserAdapter;
import com.example.skillswap.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
                        if (user != null) {
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


        } else {
            welcomeTextView.setText("Hello, guest user!");
            profileImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Replace ProfileActivity with the actual Profile activity class
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void searchUsers(String skill) {
        skill = skill.toLowerCase(); // convert skill to lowercase for case-insensitive search
        DatabaseReference userskills = FirebaseDatabase.getInstance().getReference("userskills").child(skill);
        userskills.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uid = snapshot.getKey();
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            User user = userSnapshot.getValue(User.class);
                            if (user != null) {
                                userList.add(user);
                            }
                            userAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("MainActivityClass", "Firebase error: " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("MainActivityClass", "Firebase error: " + databaseError.getMessage());
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
