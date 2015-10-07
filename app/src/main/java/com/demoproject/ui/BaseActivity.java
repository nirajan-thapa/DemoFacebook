package com.demoproject.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demoproject.gcm.RegistrationIntentService;
import com.demoproject.gcm.command.NotificationCommand;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.demoproject.R;
import com.demoproject.util.BusProvider;
import com.demoproject.util.Log;
import com.demoproject.util.PreferencesUtility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Base activity that implements the common functionality
 *
 * Created by Nirajan on 10/3/15.
 */
public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "BaseActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    // Primary toolbar
    @Bind(R.id.default_toolbar)
    Toolbar mActionBarToolbar;

    // Navigation drawer container layout
    @Nullable
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    // Navigation drawer items layout container
    @Nullable
    @Bind(R.id.nav_view)
    NavigationView mNavigationView;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    @Nullable
    @Bind(R.id.profile_picture)
    ProfilePictureView profilePicture;

    @Nullable
    @Bind(R.id.full_name)
    TextView fullName;

    protected CallbackManager callbackManager;
    // Current Facebook user profile
    private Profile userProfile;
    // Profile tracker
    private ProfileTracker profileTracker;
    // Access token tracker
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    // Boolean to track if activity is resumed
    private boolean isResumed = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        // initialize the Google Analytics Manager
        //AnalyticsManager.initializeAnalyticsTracker(getApplicationContext());

        if (savedInstanceState == null && checkPlayServices()) {
            // Register for Google Cloud Messaging
            registerForGCM();
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String token = intent.getStringExtra("GCM_TOKEN");
                Log.d(TAG, "Broadcast Sent Token: " + token);
                String storedToken = PreferencesUtility.getGCMToken(BaseActivity.this);
                Log.d(TAG, "SharedPreference Token: " + storedToken);
                if (storedToken == null || TextUtils.isEmpty(storedToken)) {
                    // Store the GCM token sent through the Registration Broadcast intent in Shared Preference
                    PreferencesUtility.setGCMToken(BaseActivity.this, token);
                }

            }
        };

        callbackManager = CallbackManager.Factory.create();

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.d(TAG, "Current profile changed");
                if (currentProfile != null) {
                    Profile.setCurrentProfile(currentProfile);
                    updateUI();
                }

            }
        };
        // Track the facebook access token
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                Log.d(TAG, "Access token changed");
                if (isResumed) {
                    if (currentAccessToken != null) {
                        if (!currentAccessToken.isExpired()) {
                            AccessToken.setCurrentAccessToken(currentAccessToken);
                            // Fetch current profile
                            Profile.fetchProfileForCurrentAccessToken();
                        }
                    } else {
                        // show log in page
                        startLoginActivity();
                    }
                }
            }
        };
        // Set the current user profile
        Profile.fetchProfileForCurrentAccessToken();
        //Log.d(TAG, "Profile Name: " + Profile.getCurrentProfile().getName());

        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);
        ButterKnife.bind(this);
        getActionBarToolbar();
    }

    /**
     * Retrieves the toolbar
     * @return Toolbar
     */
    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar != null) {
            setSupportActionBar(mActionBarToolbar);
        }
        return mActionBarToolbar;
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i(TAG, "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(getString(R.string.gcm_registration_complete)));
        isResumed = true;
        // Register Bus provider
        BusProvider.getInstance().register(this);
        // Recommended to check for Play Services on Activity's resume too
        checkPlayServices();
        updateUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        isResumed = false;
        Log.i(TAG, "onPause");
        // Always unregister when an object no longer should be on the bus.
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
        accessTokenTracker.stopTracking();
    }

    @Override
    public void onBackPressed(){
        if(mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
        getHashKey();
        //trySetupSwipeRefresh();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Registers the app for GCM
     */
    private void registerForGCM() {
        String gcmToken = PreferencesUtility.getGCMToken(this);
        Log.i(TAG, "Current Token: " + gcmToken);
        if (gcmToken == null || TextUtils.isEmpty(gcmToken)) {
            Log.d(TAG, "Registering for GCM");
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * Updates the user's profile picture and full name
     */
    private void updateUI() {
        // current Facebook user profile
        userProfile = Profile.getCurrentProfile();
        if (userProfile != null && profilePicture != null && fullName != null) {
            profilePicture.setProfileId(userProfile.getId());
            fullName.setText(userProfile.getName());
        }
    }

    /**
     * Set up the navigation view layout and items
     */
    private void setupNavDrawer() {
        if (mDrawerLayout == null) {
            return;
        }
        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mActionBarToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        // Set up navigation view header layout click listener

        // Check if the drawer welcome preference is true or false
        if(!PreferencesUtility.isDrawerWelcomeDone(this) ){
            mDrawerLayout.openDrawer(GravityCompat.START);
            PreferencesUtility.markDrawerWelcomeDone(this, true);
        }
    }

    /**
     * Get the Hash Key of the machine
      */
    private void getHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:",
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigation Drawer item click listener
     * @param menuItem clicked item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Handle navigation view item clicks here.
        final int id = menuItem.getItemId();
        Log.d(TAG, "Nav Item Id: " + String.valueOf(id));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (id == R.id.nav_status) {
                    Log.i(TAG, "Launching MainActivity");
                    Intent intent = new Intent(BaseActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } else if (id == R.id.nav_friends) {
                    Log.i(TAG, "Launching FriendsActivity");
                    Intent intent = new Intent(BaseActivity.this, FriendsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } else if (id == R.id.nav_photos) {
                    Log.i(TAG, "Launching PhotoActivity");
                    Intent intent = new Intent(BaseActivity.this, PhotoActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } else if (id == R.id.nav_settings) {
                    Log.i(TAG, "Launching Settings");
                    Intent intent = new Intent(BaseActivity.this, SettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } else if (id == R.id.nav_logout) {
                    Log.i(TAG, "Logging Out");
                    logOutUser();
                } else if (id == R.id.nav_notification) {
                    Log.i(TAG, "Preview Notification");
                    NotificationCommand.sendNotification(BaseActivity.this, "Test Notification");
                } else if (id == R.id.nav_token) {
                    Log.i(TAG, "View Registration Token");
                    displayRegistrationToken();
                }
            }
        }, NAVDRAWER_LAUNCH_DELAY);

        return true;
    }

    /**
     * Click handler for the navigation view header items
     */
    @Nullable
    @OnClick({R.id.profile_picture, R.id.full_name, R.id.view_profile})
    public void navHeaderClick() {
        Log.d(TAG, "Navigation Header Item clicked");
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        startActivity(profileIntent);
    }

    /**
     * Logs out the current user
     */
    private void logOutUser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.app_name));
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("YES",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Log.d(TAG, "Logging Out");
                LoginManager.getInstance().logOut();
                startLoginActivity();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Display Device Registration Token
     */
    private void displayRegistrationToken() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Current GCM Registration token");
        builder.setMessage("(Press and hold to copy text)");

        TextView text = new TextView(this);
        text.setPadding(20, 20, 20, 20);
        String token = PreferencesUtility.getGCMToken(this);
        text.setText(token);
        text.setTextIsSelectable(true);
        builder.setView(text);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "OK");
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Starts the Login activity
     */
    protected void startLoginActivity() {
        Intent intent = new Intent(this, FacebookLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices(){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS){
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}
