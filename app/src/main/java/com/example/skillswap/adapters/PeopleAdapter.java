package com.example.skillswap.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillswap.R;
import com.example.skillswap.models.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {
    private List<String> peopleList = new ArrayList<>();
    private Context context;
    private FirebaseUser currentUser;

    public PeopleAdapter(List<String> peopleList, Context context, FirebaseUser currentUser) {
        this.peopleList = peopleList;
        this.context = context;
        this.currentUser = currentUser;
    }

    public void addUser(String uid) {
        if (!peopleList.contains(uid)) {
            peopleList.add(uid);
            notifyItemInserted(peopleList.size() - 1);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String uid = peopleList.get(position);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);

                    SharedPreferences sharedPref = context.getSharedPreferences("userSkills", Context.MODE_PRIVATE);
                    String skillsJson = sharedPref.getString(uid, "[]"); // Default to empty JSON array if no skills are found
                    List<String> skills = new Gson().fromJson(skillsJson, new TypeToken<List<String>>(){}.getType());
                    holder.skillTextView.setText("Skills: " + String.join(", ", skills));

                    holder.nameTextView.setText("Name: " + firstName + " " + lastName);
                    holder.emailTextView.setText("Email ID: " + email);

                    holder.emailTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
                            emailIntent.setType("text/plain");
                            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                            context.startActivity(emailIntent);
                        }
                    });

                    holder.removeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = holder.getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) { // Check if item still exists
                                String uid = peopleList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, peopleList.size());
                                DatabaseReference connectionsRef = FirebaseDatabase.getInstance().getReference("connections").child(currentUser.getUid());
                                connectionsRef.child(uid).removeValue();
                            }
                        }
                    });

                    holder.sendEmailButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                            emailIntent.setData(Uri.parse("mailto:" + email));
                            context.startActivity(Intent.createChooser(emailIntent, "Send email"));
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

    @Override
    public int getItemCount() {
        return peopleList.size();
    }

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView nameTextView;
    TextView emailTextView;
    TextView skillTextView;
    Button removeButton;
    Button sendEmailButton;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.nameTextView);
        emailTextView = itemView.findViewById(R.id.emailTextView);
        removeButton = itemView.findViewById(R.id.removeButton);
        skillTextView = itemView.findViewById(R.id.skillTextView);
        sendEmailButton = itemView.findViewById(R.id.sendEmailButton);
    }
}
}

