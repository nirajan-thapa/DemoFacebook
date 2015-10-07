package com.demoproject.model;

import android.text.TextUtils;

import com.demoproject.util.Log;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nirajan on 10/5/2015.
 */
public class UserProfile {

    @SerializedName(Fields.ID)
    public String id;
    @SerializedName(Fields.NAME)
    public String name;
    @SerializedName(Fields.EMAIL)
    public String email;
    @SerializedName(Fields.BIRTHDAY)
    public String birthday;
    @SerializedName(Fields.GENDER)
    public String gender;
    @SerializedName(Fields.BIO)
    public String bio;
    @SerializedName(Fields.COVER)
    public Cover cover;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getGender() {
        return gender;
    }

    public String getBio() {
        return bio;
    }

    public Cover getCover() {
        return cover;
    }

    /**
     * Represents the different fields associated with a UserProfile
     */

    public static class Fields {
        private final String fields;

        private Fields() {
            List<String> allFields = new ArrayList<String>();
            allFields.add(ID);
            allFields.add(NAME);
            allFields.add(EMAIL);
            allFields.add(BIRTHDAY);
            allFields.add(GENDER);
            allFields.add(BIO);
            allFields.add(COVER);

            fields = TextUtils.join(",", allFields);
            Log.d("USER", "All Fields: " + fields);
        }

        public String getFields() {
            return fields;
        }

        public static Fields getInstance() {
            return new Fields();
        }

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String BIRTHDAY = "birthday";
        public static final String GENDER = "gender";
        public static final String BIO = "bio";
        public static final String COVER = "cover";

    }

    /**
     * Represents the cover photo response
     */
    public class Cover {

        @SerializedName("id")
        private String id;
        @SerializedName("offset_y")
        private Integer offsetY;
        @SerializedName("source")
        private String source;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Integer getOffsetY() {
            return offsetY;
        }

        public void setOffsetY(Integer offsetY) {
            this.offsetY = offsetY;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

    }
}
