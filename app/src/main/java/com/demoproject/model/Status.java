package com.demoproject.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Nirajan on 10/6/2015.
 */
public class Status {
    @SerializedName("message")
    public String message;
    @SerializedName("story")
    public String story;
    @SerializedName("created_time")
    public String date;

    public String getMessage() {
        return message;
    }

    public String getStory() {
        return story;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
