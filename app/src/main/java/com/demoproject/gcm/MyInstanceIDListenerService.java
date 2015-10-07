package com.demoproject.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Nirajan on 10/6/2015.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {
    private static final String TAG = "MyInstanceIDListenerService";

    /**
     * Called if InstanceID token is updated.
     */
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }

}
