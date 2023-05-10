package com.example.skillswap.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skillswap.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        // Setup BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnNavigationItemSelectedListener(this);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_menu_item:
                // Check if the current activity is already MainActivity
                if (!(this instanceof MainActivity)) {
                    Intent homeIntent = new Intent(this, MainActivity.class);
                    startActivity(homeIntent);
                    finish();
                }
                return true;

            case R.id.chat_menu_item:
                // TODO: Handle the chat menu item click
                return true;

            case R.id.myskill_menu_item:
                // TODO: Handle the my skill menu item click
                return true;

            case R.id.schedule_menu_item:
                // TODO: Handle the schedule menu item click
                return true;

            case R.id.profile_menu_item:
                // Check if the current activity is already ProfileActivity
                if (!(this instanceof ProfileActivity)) {
                    Intent profileIntent = new Intent(this, ProfileActivity.class);
                    startActivity(profileIntent);
                    finish();
                }
                return true;

            default:
                return false;
        }
    }
}
