package com.example.skillswap.models;

public class Skill {
    private String skill;
    private String category;

    public Skill() {
        // Default constructor required for Firebase
    }
    public Skill(String skill, String category) {
        this.skill = skill;
        this.category = category;
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
                '}';
    }
}
