package com.demoproject.gcm;

import android.os.Bundle;

import com.demoproject.gcm.command.GCMCommand;
import com.demoproject.gcm.command.NotificationCommand;
import com.demoproject.util.Log;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nirajan on 10/6/2015.
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    private static final Map<String, GCMCommand> MESSAGE_RECEIVERS;

    /**
     * These will be used to match with the action data present in GCM to start corresponding
     * command actions
     */
    private static final String COMMAND_NOTIFICATION_ACTION = "notification";

    // Known GCM messages and their receivers
    static{
        Map<String, GCMCommand> receivers = new HashMap<String, GCMCommand>();
        // regular gcm notification
        receivers.put(COMMAND_NOTIFICATION_ACTION, new NotificationCommand());
        MESSAGE_RECEIVERS = Collections.unmodifiableMap(receivers);
    }

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String action = data.getString("action");
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */
        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        if (action == null) {
            Log.e(TAG, "GCM message received without command action");
            return;
        }
        action = action.toLowerCase();
        // Get the GCM command for this action
        GCMCommand command = MESSAGE_RECEIVERS.get(action);
        if (command == null) {
            Log.e(TAG, "Unknown command received: " + action);
        } else {
            command.execute(this, action, message);
        }

    }

}
