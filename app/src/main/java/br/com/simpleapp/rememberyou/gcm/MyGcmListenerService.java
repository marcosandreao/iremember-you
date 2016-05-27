package br.com.simpleapp.rememberyou.gcm;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import br.com.simpleapp.rememberyou.BuildConfig;
import br.com.simpleapp.rememberyou.HomeActivity;
import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.service.HistoryService;
import br.com.simpleapp.rememberyou.utils.NotificationUtil;

/**
 * Created by socram on 22/03/16.
 */
public class MyGcmListenerService  extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        final String name = data.getString("name");
        final String message = data.getString("msg");
        final String emotion = data.getString("emotion");
        final String email = data.getString("email");
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "From: " + from);
            Log.d(TAG, "name: " + name);
            Log.d(TAG, "Message: " + message);
            Log.d(TAG, "emotion: " + emotion);
            Log.d(TAG, "email: " + email);
        }

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }
        sendNotification(name, message, emotion);

        try {
            final HistoryService service = new HistoryService();
            service.save(name, email, emotion );
            Log.d(TAG, data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(final String name, final String message, final String emotion) {

        final Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent,
                PendingIntent.FLAG_ONE_SHOT);


        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        final String ringstonePreference = sharedPreferences.getString("notifications_ringtone", "");

        new  NotificationUtil.DecodeResourseToBitmap(this, new NotificationUtil.IDecodeResourseToBitmap() {

            @Override
            public void onFinish(Bitmap mBitmap) {
                Uri soundUri = null;
                if (TextUtils.isEmpty(ringstonePreference) ) {
                    soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                } else {
                    soundUri = Uri.parse(ringstonePreference);
                }

                final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MyGcmListenerService.this)
                        .setSmallIcon(R.drawable.ic_insert_emoticon_white_24dp)
                        .setLargeIcon(mBitmap)
                        .setContentTitle(name)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(soundUri)
                        .setContentIntent(pendingIntent);

                final NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify( (int) System.currentTimeMillis(), notificationBuilder.build());
            }
        }).execute(emotion);


    }
}