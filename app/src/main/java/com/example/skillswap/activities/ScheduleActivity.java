package com.example.skillswap.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import com.example.skillswap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class ScheduleActivity extends BaseActivity {

    private CalendarView calendarView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_schedule);

            Button addEvent = findViewById(R.id.addEvent);

            //Setting click listeners for the buttons
            addEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(ScheduleActivity.this, CreateEventActivity.class);
                    startActivity(intent);


                }
            });


            // Obtain a reference to the CalendarView widget.
            calendarView = findViewById(R.id.calendarView);



    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.schedule_menu_item);
        }
    }
}
