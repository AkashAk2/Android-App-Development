package com.example.skillswap.activities;


import android.content.Intent;
//import android.media.metrics.Event;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skillswap.R;
import com.example.skillswap.models.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference eventRef;

    private EditText editTextTitle;
    private EditText editTextDescription;
    private DatePicker datePicker;
    private EditText editTextLocation;
    private Button buttonCancel;
    private Button buttonSave;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        eventRef = FirebaseDatabase.getInstance().getReference().child("events");

        // Initialize views
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        datePicker = findViewById(R.id.datePicker);
        editTextLocation = findViewById(R.id.editTextLocation);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonSave = findViewById(R.id.buttonSave);

        // Set OnClickListener for the Cancel button
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Login activity
                Intent intent = new Intent(CreateEventActivity.this, ScheduleActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Set OnClickListener for the Save button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve input values
                String title = editTextTitle.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();
                String location = editTextLocation.getText().toString().trim();
                String emailid = "testemail@gmail.com";

                // Get selected date from DatePicker
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();

                // Create a Calendar instance and set the selected date
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);

                // Get the timestamp representing the selected date
                long timestamp = calendar.getTimeInMillis();

                // Create an Event object
                // Create a new instance of the Event class
                Event newEvent = new Event(emailid, title, description, location, timestamp);

                // Generate a unique identifier for the event
                String eventId = eventRef.push().getKey();

                // Save the event to Firebase and add a completion listener
                eventRef.child(eventId).setValue(newEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Write was successful!
                            Toast.makeText(CreateEventActivity.this, "Event saved successfully", Toast.LENGTH_SHORT).show();

                            // Navigate back to ScheduleActivity
                            Intent intent = new Intent(CreateEventActivity.this, ScheduleActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Write failed
                            Toast.makeText(CreateEventActivity.this, "Failed to save event. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("Firebase", task.getException().toString());
                        }
                    }
                });
            }
        });
    }
}
