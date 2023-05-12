package com.example.skillswap.models;

public class Skill {
    private String skill;
    private String category;

    private long addedDate;

    private boolean isEnabled;

    private String skillId;


    public Skill(){

    }
    public Skill(String skill, String category) {
        this.skill = skill;
        this.category = category;
    }

    public Skill(String skill, String category, long addedDate, boolean isEnabled, String skillId) {
        this.skill = skill;
        this.category = category;
        this.addedDate = addedDate;
        this.isEnabled = isEnabled;
        this.skillId = skillId;
    }

    public long getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(long addedDate) {
        this.addedDate = addedDate;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

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
                ", addedDate=" + addedDate +
                ", isEnabled=" + isEnabled +
                ", skillId='" + skillId + '\'' +
                '}';
    }
}