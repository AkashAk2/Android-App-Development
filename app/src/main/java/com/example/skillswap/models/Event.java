package com.example.skillswap.models;

public class Event {
    private String emailid;
    private String title;
    private String description;
    private String location;
    private long timestamp;


    public Event(){
        this.emailid = "";
        this.title = "";
        this.description = "";
        this.location = "";
        this.timestamp = 0;
    }

    public Event(String emailid,String title, String description, String location, long timestamp){
        this.emailid = emailid;
        this.title = title;
        this.description = description;
        this.location = location;
        this.timestamp = timestamp;
    }

    public String getEventEmail() {
        return emailid;
    }

    public String getEventTitle() {
        return title;
    }

    public void setEventTitle(String title) {
        this.title = title;
    }

    public String getEventDescription() {
        return description;
    }

    public void setEventDescription(String description) {
        this.description = description;
    }

    public String getEventLocation() {
        return location;
    }

    public void setEmail(String emailid) {
        this.emailid = emailid;
    }

    public void setEventLocation(String location) {
        this.location = location;
    }

    public long getEventTimestamp() {
        return timestamp;
    }

    public void setEventTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


}

