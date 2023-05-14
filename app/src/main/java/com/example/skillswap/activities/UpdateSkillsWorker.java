package com.example.skillswap;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class UpdateSkillsWorker extends Worker {

    public UpdateSkillsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("userSkills", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String uid = userSnapshot.getKey();

                    DatabaseReference skillsRef = FirebaseDatabase.getInstance().getReference("userskills").child(uid);
                    skillsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                List<String> skills = new ArrayList<>();
                                for (DataSnapshot skillSnapshot : dataSnapshot.getChildren()) {
                                    String skill = skillSnapshot.child("skill").getValue(String.class);
                                    skills.add(skill);
                                }
                                editor.putString(uid, new Gson().toJson(skills));
                                editor.apply();
                            }
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

        return Result.success();
    }
}
