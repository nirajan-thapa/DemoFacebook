package com.demoproject.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Nirajan on 10/5/2015.
 */
public class Photo {

    @SerializedName("id")
    public String id;
    @SerializedName("source")
    public String photoLink;

    public Photo(String id, String photoLink) {
        this.id = id;
        this.photoLink = photoLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    @Override
    public String toString() {
        return "ID: " + id + " Link: " + photoLink;
    }
}
