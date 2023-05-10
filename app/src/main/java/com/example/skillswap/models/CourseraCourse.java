package com.example.skillswap.models;

public class CourseraCourse {
    private String id;
    private String name;
    private String photoUrl;
    private boolean isPaid;
    private String partner;

    public CourseraCourse(String id, String name, String photoUrl, boolean isPaid, String partner) {
        this.id = id;
        this.name = name;
        this.photoUrl = photoUrl;
        this.isPaid = isPaid;
        this.partner = partner;
    }

    public CourseraCourse() {
        this("", "", "", false, "");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }
}

