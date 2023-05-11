package com.example.skillswap.activities;

import android.os.Bundle;
import android.widget.CalendarView;

import com.example.skillswap.R;

public class ScheduleActivity extends BaseActivity {

    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // Obtain a reference to the CalendarView widget.
        calendarView = findViewById(R.id.calendarView);
    }
}
