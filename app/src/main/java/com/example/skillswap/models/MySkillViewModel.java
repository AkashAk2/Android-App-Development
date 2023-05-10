package com.example.skillswap.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class MySkillViewModel extends ViewModel {
    private MutableLiveData<Skill> mSkillMutableLiveData = new MutableLiveData<>(null);
    public LiveData<Skill> mSkillLiveData = mSkillMutableLiveData;

    private MutableLiveData<Skill> tSkillMutableLiveData = new MutableLiveData<>(null);
    public LiveData<Skill> tSkillLiveData = tSkillMutableLiveData;

    public void setSelectedSkill(Skill skill){
        mSkillMutableLiveData.setValue(skill);
    }
    public void resetSkill(){
        mSkillMutableLiveData.setValue(null);
    }

    public void setTeachSkill(Skill skill){
        tSkillMutableLiveData.setValue(skill);
    }
    public void resetTeachSkill(){
        tSkillMutableLiveData.setValue(null);
    }

    private MutableLiveData<List<String>> mListMutableLiveData = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<String>> mListLiveData = mListMutableLiveData;

    public void addToList(String skill) {
        List<String> list = mListMutableLiveData.getValue();
        list.add(skill);
        mListMutableLiveData.setValue(list);
    }

    private MutableLiveData<List<String>> tListMutableLiveData = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<String>> tListLiveData = tListMutableLiveData;

    public void addToTeachList(String skill) {
        List<String> list = tListMutableLiveData.getValue();
        list.add(skill);
        tListMutableLiveData.setValue(list);
    }
}
