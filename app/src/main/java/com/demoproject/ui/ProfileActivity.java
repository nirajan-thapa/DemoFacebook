package com.demoproject.ui;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ImageView;

import com.demoproject.R;
import com.demoproject.model.UserData;
import com.demoproject.model.UserProfile;
import com.demoproject.ui.adapter.ProfileDetailAdapter;
import com.demoproject.util.Log;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;


public class ProfileActivity extends BaseActivity {

    private static final String TAG = "ProfileActivity";

    @Bind(R.id.profile_picture_detail)
    ProfilePictureView profilePictureView;
    @Bind(R.id.cover_picture)
    ImageView coverImage;

    Profile currentProfile;

    @Bind(R.id.profile_recycler_view)
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    private ProfileDetailAdapter mAdapter;

    // to save all the user's profile details
    UserProfile userProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentProfile = Profile.getCurrentProfile();
        //getActionBarToolbar().setTitle(currentProfile.getName());
        profilePictureView.setProfileId(currentProfile.getId());
        Log.d(TAG, "Profile ID: " + currentProfile.getId());

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ProfileDetailAdapter();
        mRecyclerView.setAdapter(mAdapter);

        makeUserDetailAPICall();

    }

    private void makeUserDetailAPICall() {
        Bundle param = new Bundle();
        UserProfile.Fields fields = UserProfile.Fields.getInstance();
        param.putString("fields", fields.getFields());
        Log.d(TAG, "Fields: " + fields.getFields());

        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        JSONObject jsonObject = response.getJSONObject();
                        if (jsonObject != null) {
                            Log.d(TAG, "Graph MeRequest JSON Response: " + jsonObject.toString());
                            String responseString = jsonObject.toString();
                            Gson gson = new Gson();
                            userProfile = gson.fromJson(responseString, UserProfile.class);
                            Log.d(TAG, "ID: " + userProfile.getId());
                            Log.d(TAG, "Birthday: " + userProfile.getBirthday());
                            addUserInfoToAdapter(userProfile);
                        }
                    }
                });
        graphRequest.setParameters(param);
        graphRequest.executeAsync();
    }

    private void addUserInfoToAdapter(UserProfile profile) {
        // Add name
        String name = profile.getName();
        if (name != null && !name.isEmpty()) {
            mAdapter.addData(new UserData(UserProfile.Fields.NAME.toUpperCase(), name ));
        }
        // Add email
        String email = profile.getEmail();
        if (email != null && !email.isEmpty()) {
            mAdapter.addData(new UserData(UserProfile.Fields.EMAIL.toUpperCase(), email ));
        }
        // Add birthday
        String birthday = profile.getBirthday();
        if (birthday != null && !birthday.isEmpty()) {
            mAdapter.addData(new UserData(UserProfile.Fields.BIRTHDAY.toUpperCase(), birthday ));
        }
        // Add gender
        String gender = profile.getGender();
        if (gender != null && !gender.isEmpty()) {
            mAdapter.addData(new UserData(UserProfile.Fields.GENDER.toUpperCase(), gender ));
        }
        // Add bio
        String bio = profile.getBio();
        if (bio != null && !bio.isEmpty()) {
            mAdapter.addData(new UserData(UserProfile.Fields.BIO.toUpperCase(), bio ));
        }
        // Get cover photo
        UserProfile.Cover cover = profile.getCover();
        String coverUri = cover.getSource();
        Picasso.with(this)
                .load(coverUri)
                .into(coverImage);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * click listeners for pictures
     */
    @OnClick(R.id.profile_picture_detail)
    public void photoClick() {
        String photoLink = currentProfile.getProfilePictureUri(500, 500).toString();
        startFullScreenActivity(photoLink);
    }

    @OnClick(R.id.cover_picture)
    public void coverClick() {
        String coverLink = userProfile.getCover().getSource();
        startFullScreenActivity(coverLink);
    }

    private void startFullScreenActivity(String photoURL) {
        if (photoURL != null && !photoURL.isEmpty()){
            Intent intent = new Intent(this, FullScreenActivity.class);
            intent.putExtra(FullScreenActivity.KEY_PHOTO_URL, photoURL);
            startActivity(intent);
        }
    }
}
