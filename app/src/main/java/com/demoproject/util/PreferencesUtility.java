package com.demoproject.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.demoproject.R;

/**
 * Created by Nirajan on 10/3/15.
 */
public class PreferencesUtility {

    /**
     * Checks if the drawer was opened for first-time install
     */
    public static boolean isDrawerWelcomeDone(final Context context){
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
        return mPref.getBoolean(context.getString(R.string.pref_drawer_welcome), false);
    }

    /**
     * Sets the flag for drawer open first-time install
     */
    public static void markDrawerWelcomeDone(final Context context, boolean flag){
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
        mPref.edit().putBoolean(context.getString(R.string.pref_drawer_welcome), flag).commit();
    }

    public static int getCurrentDrawerActivityId(final Context context) {
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
        return mPref.getInt(context.getString(R.string.pref_current_activity), R.id.nav_status);
    }

    public static void setCurrentDrawerActivityId(final Context context, int id) {
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
        mPref.edit().putInt(context.getString(R.string.pref_current_activity), id).commit();
    }

    public static boolean isGCMTokenSentToServer(final Context context) {
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
        return mPref.getBoolean(context.getString(R.string.gcm_token_sent), false);
    }
    public static void setGCMTokenSentToServer(final Context context, boolean isSent) {
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
        mPref.edit().putBoolean(context.getString(R.string.gcm_token_sent), true);
    }

    public static String getGCMToken(final Context context) {
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
        return mPref.getString(context.getString(R.string.gcm_token), null);
    }

    public static void setGCMToken(final Context context, String deviceID) {
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
        mPref.edit().putString(context.getString(R.string.gcm_token), deviceID).commit();
    }

}
