package com.example.skillswap.models;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MySkillViewModel extends ViewModel {

    DatabaseReference mySkillRefs = FirebaseDatabase.getInstance().getReference("users").child("mySkills");
    DatabaseReference teachSkillRefs =  FirebaseDatabase.getInstance().getReference("users").child("teachSkillsRefs");

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


    void addSkillToUser(Skill skill, SkillType skillType, Result.CallBack callBack) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null)
            return;

        if (skillType == SkillType.LEARN_SKILL) {
            if (mListLiveData.getValue() == null)
                return;
            if (mListLiveData.getValue().size() == 4) {
                callBack.response(new Result.Error(new Exception("Only 4 skills can be added!")));
                return;
            }
            if(mListLiveData.getValue().contains(skill.getSkill())){
                callBack.response(new Result.Error(new Exception("Skill already available!")));
                return;
            }
            String uniqueId = mySkillRefs.child(firebaseAuth.getCurrentUser().getUid()).push().getKey();
            skill.setSkillId(uniqueId);
            mySkillRefs.child(firebaseAuth.getCurrentUser().getUid()).child(uniqueId).setValue(skill).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
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
            if (tListLiveData.getValue().size() == 4) {
                callBack.response(new Result.Error(new Exception("Only 4 skills can be added!")));
                return;
            }
            if(tListLiveData.getValue().contains(skill.getSkill())){
                callBack.response(new Result.Error(new Exception("Skill already available!")));
                return;
            }
            String uniqueId = teachSkillRefs.child(firebaseAuth.getCurrentUser().getUid()).push().getKey();
            skill.setSkillId(uniqueId);
            teachSkillRefs.child(firebaseAuth.getCurrentUser().getUid()).child(uniqueId).setValue(skill).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
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

        if (skillType == SkillType.LEARN_SKILL) {
            mySkillRefs.child(firebaseAuth.getCurrentUser().getUid()).child(skill.getSkillId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    callBack.response(new Result.Success(true));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callBack.response(new Result.Error(new Exception("Failed to delete skill!")));
                }
            });
        } else {
            teachSkillRefs.child(firebaseAuth.getCurrentUser().getUid()).child(skill.getSkillId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
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

        mySkillRefs.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
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

        teachSkillRefs.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
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

