package com.demoproject.util;

import android.app.Activity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.lang.ref.WeakReference;
import java.util.Arrays;

/**
 * Created by Nirajan on 10/3/15.
 */
public class FacebookSessionManager implements FacebookCallback<LoginResult>{

    private static final String TAG = "FacebookSessionManager";

    private WeakReference<Activity> mActivity;
    private final LoginManager mLoginManager;
    private final CallbackManager mCallbackManager;

    private static String loginResponse;

    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.d(TAG, "Login Success");
        loginResponse = loginResult.toString();
    }
    @Override
    public void onCancel() {
        Log.d(TAG, "UserProfile cancelled login");
    }
    @Override
    public void onError(FacebookException error) {
        Log.e(TAG, "Login error");
    }

    public FacebookSessionManager() {
        //BusProvider.getInstance().register(this);
        mLoginManager = LoginManager.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        //mLoginManager.registerCallback(mCallbackManager, callback);
    }

    public void setActivity(Activity activity) {
        mActivity = new WeakReference<Activity>(activity);
    }

    public Activity getActivity() {
        return mActivity.get();
    }

    public CallbackManager getmCallbackManager() {
        return mCallbackManager;
    }

    /**
     * Logs the user facebook account
     *
     * @param activity the activity tied to the API call
     */
    public void doLogIn(Activity activity) {
        mLoginManager.logInWithReadPermissions(getActivity(), Arrays.asList("email", "user_photos", "public_profile"));
    }

    /**
     * Check if the user is currently logged in
     *
     * @return boolean
     */
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            return false;
        }
        return !accessToken.isExpired();
    }
}
