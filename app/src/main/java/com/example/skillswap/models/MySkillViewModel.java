package com.example.skillswap.models;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.skillswap.adapters.SkillAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MySkillViewModel extends ViewModel {

    DatabaseReference learnskills = FirebaseDatabase.getInstance().getReference("learnskills");
    DatabaseReference userskills =  FirebaseDatabase.getInstance().getReference("userskills");

    public enum SkillType {
        LEARN_SKILL, TEACH_SKILL
    }

    public void setSelectedSkill(Skill skill, SkillType skillType, Result.CallBack callBack) {
        skill.setEnabled(true);
        skill.setAddedDate(System.currentTimeMillis());
        addSkillToUser(skill, skillType, callBack);
    }

    private MutableLiveData<List<Skill>> mListMutableLiveData = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Skill>> mListLiveData = mListMutableLiveData;

    private MutableLiveData<List<Skill>> tListMutableLiveData = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Skill>> tListLiveData = tListMutableLiveData;

    private MutableLiveData<SkillType> mSkillTypeMutableLiveData = new MutableLiveData<>(SkillType.LEARN_SKILL);
    public LiveData<SkillType> mSkillTypeLiveData = mSkillTypeMutableLiveData;



    public void setSelectedSkillType(SkillType skillType){
        mSkillTypeMutableLiveData.setValue(skillType);
    }


    public void addSkillToUser(Skill skill, SkillType skillType, Result.CallBack callBack) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null)
            return;

        if (skillType == SkillType.LEARN_SKILL) {
            if (mListLiveData.getValue() == null)
                return;
            if (mListLiveData.getValue().stream().filter(Skill::isEnabled).count() >= 4) {
                callBack.response(new Result.Error(new Exception("Only 4 skills can be added!")));
                return;
            }
            if(mListLiveData.getValue().stream().filter(Skill::isEnabled).map(Skill::getSkill).collect(Collectors.toList()).contains(skill.getSkill())){
                callBack.response(new Result.Error(new Exception("Skill already available!")));
                return;
            }
            String uniqueId = learnskills.child(firebaseAuth.getCurrentUser().getUid()).push().getKey();
            skill.setSkillId(uniqueId);
            learnskills.child(firebaseAuth.getCurrentUser().getUid()).child(uniqueId).setValue(skill).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    // Add the user to the skill in the "skills" node
                    learnskills.child(skill.getSkill().toLowerCase()).child(firebaseAuth.getCurrentUser().getUid()).setValue(true);
                    callBack.response(new Result.Success("Skill added successfully!"));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callBack.response(new Result.Error(new Exception("Failed to add skill!")));
                }
            });
        } else {
            if (tListLiveData.getValue() == null)
                return;
            if (tListLiveData.getValue().stream().filter(Skill::isEnabled).count() >= 4) {
                callBack.response(new Result.Error(new Exception("Only 4 skills can be added!")));
                return;
            }
            if(tListLiveData.getValue().stream().filter(Skill::isEnabled).map(Skill::getSkill).collect(Collectors.toList()).contains(skill.getSkill())){
                callBack.response(new Result.Error(new Exception("Skill already available!")));
                return;
            }

            String uniqueId = userskills.child(firebaseAuth.getCurrentUser().getUid()).push().getKey();
            skill.setSkillId(uniqueId);
            userskills.child(firebaseAuth.getCurrentUser().getUid()).child(uniqueId).setValue(skill).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    // Add the user to the skill in the "skills" node
                    userskills.child(skill.getSkill().toLowerCase()).child(firebaseAuth.getCurrentUser().getUid()).setValue(true);
                    callBack.response(new Result.Success("Skill added successfully!"));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callBack.response(new Result.Error(new Exception("Failed to add skill!")));
                }
            });
        }
    }

    public void removeSkillForUser(Skill skill, SkillType skillType, Result.CallBack callBack) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null)
            return;

        skill.setEnabled(false);
        if (skillType == SkillType.LEARN_SKILL) {
            learnskills.child(firebaseAuth.getCurrentUser().getUid()).child(skill.getSkillId()).setValue(skill).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    // Remove the user from the skill in the "skills" node
                    learnskills.child(skill.getSkill().toLowerCase()).child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                    callBack.response(new Result.Success(true));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callBack.response(new Result.Error(new Exception("Failed to delete skill!")));
                }
            });
        } else {
            userskills.child(firebaseAuth.getCurrentUser().getUid()).child(skill.getSkillId()).setValue(skill).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    // Remove the user from the skill in the "skills" node
                    userskills.child(skill.getSkill().toLowerCase()).child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                    callBack.response(new Result.Success(true));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callBack.response(new Result.Error(new Exception("Failed to delete skill!")));
                }
            });
        }
    }

    public void getAllUserSkills(Result.CallBack callBack) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) return;

        learnskills.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Skill> newList = new ArrayList<>();
                for (DataSnapshot d : snapshot.getChildren()) {
                    newList.add(Objects.requireNonNull(d.getValue(Skill.class)));
                }
                mListMutableLiveData.setValue(newList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.response(new Result.Error(new Exception("Failed to fetch skills!")));
            }
        });

        userskills.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Skill> newList = new ArrayList<>();
                for (DataSnapshot d : snapshot.getChildren()) {
                    newList.add(Objects.requireNonNull(d.getValue(Skill.class)));
                }
                tListMutableLiveData.setValue(newList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.response(new Result.Error(new Exception("Failed to fetch skills!")));
            }
        });
    }

}

