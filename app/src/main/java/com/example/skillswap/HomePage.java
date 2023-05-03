package com.example.skillswap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                startActivity(intent);
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
                Intent intent = new Intent(HomePage.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}