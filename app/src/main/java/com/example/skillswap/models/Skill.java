package com.example.skillswap.models;

public class Skill {
    private String skill;
    private String category;
    private boolean isAdded;
    private long date;
    private String skillId;

    public Skill() {
        // Default constructor required for Firebase
    }
    public Skill(String skill, String category) {
        this.skill = skill;
        this.category = category;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public Skill(String skill, String category, long date, boolean isAdded, String skillId) {
        this.skill = skill;
        this.category = category;
        this.date = date;
        this.isAdded = isAdded;
        this.skillId = skillId;
    }

    public String getSkillId() {return skillId;}

    public void setSkillId(String skillId) {this.skillId = skillId;}

    public long getDate() {return date;}

    public void setDate(long date) {this.date = date;}

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "skill='" + skill + '\'' +
                ", category='" + category + '\'' +
                ", isAdded=" + isAdded +
                ", date=" + date +
                ", skillId='" + skillId + '\'' +
                '}';
    }
}
