package com.demoproject.gcm.command;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.demoproject.R;
import com.demoproject.ui.FacebookLoginActivity;

/**
 * GCM command that displays a notification
 *
 * Created by Nirajan on 10/6/2015.
 */
public class NotificationCommand extends GCMCommand{

    @Override
    public void execute(Context context, String type, String extraData) {
        sendNotification(context, extraData);
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    public static void sendNotification(Context context, String message) {
        Intent intent = new Intent(context, FacebookLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
                /*.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(message))
                .addAction(R.mipmap.ic_launcher, "DISMISS", dismissIntent)
                .addAction(R.mipmap.ic_launcher, "VIEW", pendingIntent)*/
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

}
