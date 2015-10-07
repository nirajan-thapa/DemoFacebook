package com.demoproject.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Nirajan on 10/7/2015.
 */
public class Friend {
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String id;
    @SerializedName("picture")
    private Picture picture;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }


    public class Picture {
        @SerializedName("data")
        private Data data;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }
    }
    /**
     * Data represents a friend's profile picture data
     */
    public class Data {
        @SerializedName("is_silhouette")
        private Boolean isSilhouette;
        @SerializedName("url")
        private String url;

        public Boolean getIsSilhouette() {
            return isSilhouette;
        }

        public void setIsSilhouette(Boolean isSilhouette) {
            this.isSilhouette = isSilhouette;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
