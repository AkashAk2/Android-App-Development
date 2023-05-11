package com.example.skillswap.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import com.example.skillswap.R;

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
                //Login activity
                Intent intent = new Intent(ScheduleActivity.this, AddEventActivity.class);
                startActivity(intent);
                finish();
            }
        });


        // Obtain a reference to the CalendarView widget.
        calendarView = findViewById(R.id.calendarView);
    }
}
