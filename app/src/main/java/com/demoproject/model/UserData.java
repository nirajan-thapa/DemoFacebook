package com.demoproject.model;

/**
 * Represents each piece of user profile information
 *
 * Created by Nirajan on 10/5/2015.
 */
public class UserData {

    private String title;
    private String description;

    public UserData(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
