package com.demoproject.ui;

import android.content.Intent;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.demoproject.R;
import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.demoproject.util.Log;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A Facebook login screen
 */
public class FacebookLoginActivity extends BaseActivity {

    private static final String TAG = "FacebookLoginActivity";

    @Bind(R.id.login_button)
    LoginButton loginButton;

    @Bind(R.id.wallpaper_image)
    ImageView wallpaper;

    private ProfileTracker mProfileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_facebook_login);
        ButterKnife.bind(this);

        updateWithToken(AccessToken.getCurrentAccessToken());

        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_facebook_login));

        Picasso.with(this).load(R.drawable.wallpaper).into(wallpaper);


        loginButton.setReadPermissions("public_profile, user_about_me, user_birthday, user_friends, " +
                "user_photos, user_location, user_posts, user_status, email, ");

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Login onSuccess");
                if (AccessToken.getCurrentAccessToken() != null) {
                    Log.d(TAG, "Access token: " + AccessToken.getCurrentAccessToken().getToken().toString());
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            Profile.setCurrentProfile(currentProfile);
                            Profile userProfile = Profile.getCurrentProfile();
                            Log.d(TAG, "Name: " + userProfile.getName());
                            mProfileTracker.stopTracking();
                            startMainActivity();
                        }
                    };
                    mProfileTracker.startTracking();
                }
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "UserProfile cancelled login");
                // Set access token to null
                AccessToken.setCurrentAccessToken(null);
                Profile.setCurrentProfile(null);
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e(TAG, "Exception during login " + exception.getMessage());
                Log.e(TAG, "Exception during login " + exception.getCause());
                Toast.makeText(FacebookLoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                exception.printStackTrace();
                AccessToken.setCurrentAccessToken(null);
                Profile.setCurrentProfile(null);
            }
        });

    }

    private void updateWithToken(AccessToken currentAccessToken) {
        if (currentAccessToken != null && !currentAccessToken.isExpired()) {
            finish();
            startMainActivity();
        }
    }
    /**
     * Starts the Main Activity if the user is already logged in
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        updateWithToken(AccessToken.getCurrentAccessToken());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProfileTracker != null)
            mProfileTracker.stopTracking();
    }
}

