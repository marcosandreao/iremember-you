package br.com.simpleapp.rememberyou.gcm;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import br.com.simpleapp.rememberyou.MainActivity;
import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.entity.History;
import br.com.simpleapp.rememberyou.entity.HistoryDao;
import br.com.simpleapp.rememberyou.service.HistoryService;
import br.com.simpleapp.rememberyou.utils.Emotions;

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
        String name = data.getString("name");
        String message = data.getString("msg");
        String emotion = data.getString("emotion");
        String email = data.getString("email");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "name: " + name);
        Log.d(TAG, "Message: " + message);
        Log.d(TAG, "emotion: " + emotion);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }
        sendNotification(name, message, emotion);

        try {
            final HistoryService service = new HistoryService();
            service.save(name, "marcosandreao@gmail.com", emotion );
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
    private void sendNotification(String name, String message, String emotion) {

        final Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent,
                PendingIntent.FLAG_ONE_SHOT);


        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        final String ringstonePreference = sharedPreferences.getString("notifications_ringtone", "");

        Uri soundUri = null;
        if (TextUtils.isEmpty(ringstonePreference) ) {
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        } else {
            soundUri = Uri.parse(ringstonePreference);
        }

        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_emotion_1f44d)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), Emotions.getByKey(emotion)))//TODO
                .setContentTitle(name)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify( (int) System.currentTimeMillis(), notificationBuilder.build());
    }
}