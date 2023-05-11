package com.example.skillswap.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.skillswap.R;

public class CreateEventActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private DatePicker datePicker;
    private EditText editTextLocation;
    Button buttonCancel = findViewById(R.id.buttonCancel);
    Button buttonSave = findViewById(R.id.buttonSave);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Login activity
                Intent intent = new Intent(CreateEventActivity.this, ScheduleActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
