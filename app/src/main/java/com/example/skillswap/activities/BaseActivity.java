package com.example.skillswap.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.skillswap.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    protected BottomNavigationView bottomNavigationView;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

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

            case R.id.nearby_places_menu_item:
                if (!(this instanceof NearbyPlacesActivity)) {
                    Intent nearbyPlacesIntent = new Intent(this, NearbyPlacesActivity.class);
                    startActivity(nearbyPlacesIntent);
                    finish();
                }
                return true;

            case R.id.myskill_menu_item:
                if (!(this instanceof MySkillActivity)){
                    Intent mySkillIntent = new Intent(this, MySkillActivity.class);
                    startActivity(mySkillIntent);
                    finish();
                }
                return true;

            case R.id.schedule_menu_item:
                if (!(this instanceof ScheduleActivity)){
                    Intent scheduleIntent = new Intent(this, ScheduleActivity.class);
                    startActivity(scheduleIntent);
                    finish();
                }
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
