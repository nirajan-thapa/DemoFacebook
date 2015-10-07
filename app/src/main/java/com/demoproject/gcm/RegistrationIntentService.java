package com.demoproject.gcm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.demoproject.R;
import com.demoproject.util.Log;
import com.demoproject.util.PreferencesUtility;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by Nirajan on 10/6/2015.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegistrationIntentService";

    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        String token = "";

        try {
            // In case multiple refresh operations occur simultaneously
            // ensure they are processed sequentially
            synchronized (TAG) {
                // Initial network call to retrieve the token
                // subsequent calls are local
                InstanceID instanceID = InstanceID.getInstance(this);
                token = instanceID.getToken(getString(R.string.gcm_sender_id),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                Log.i(TAG, "GCM Registration Token: " + token);
                // ToDo: Send Analytics Event for successful GCM Registration
                //AnalyticsManager.sendEvent("GCM", TAG, "Success");

                // ToDo: Send Registration ID to the server
                 /*if (!PreferencesUtility.isGCMTokenSentToServer(this)) {
                     sendRegistrationToServer(token);
                 }*/

                // Subscribe to topic channels
                subscribeTopics(token);

                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                PreferencesUtility.setGCMTokenSentToServer(this, true);

                // Storing the GCM token for testing purposes
                PreferencesUtility.setGCMToken(this, token);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to complete token refresh");
            PreferencesUtility.setGCMTokenSentToServer(this, false);
            // ToDo: Send Exception to AnalyticsManager
            //AnalyticsManager.sendCaughtException(TAG + " onHandleIntent", true);
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(getString(R.string.gcm_registration_complete));
        registrationComplete.putExtra("GCM_TOKEN", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

}
