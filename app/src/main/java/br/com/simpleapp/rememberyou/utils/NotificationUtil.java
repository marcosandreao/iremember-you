package br.com.simpleapp.rememberyou.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import br.com.simpleapp.rememberyou.MainActivity;
import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.service.SendMessageReceiver;

/**
 * Created by socram on 26/04/16.
 */
public class NotificationUtil {

    public static void pinNotification(Context ctx, int id, String email, String name, String emotion) {

        final Intent intent = new Intent(ctx, MainActivity.class);
        intent.putExtra(SendMessageReceiver.BUNDLE_ID, id);
        intent.putExtra(SendMessageReceiver.BUNDLE_EMAIL, email);
        intent.putExtra(SendMessageReceiver.BUNDLE_EMOTION, emotion);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(ctx, id, intent,
                PendingIntent.FLAG_ONE_SHOT);

        final Intent intentAction = new Intent(ctx, SendMessageReceiver.class);
        intentAction.putExtra(SendMessageReceiver.BUNDLE_ID, id);
        intentAction.putExtra(SendMessageReceiver.BUNDLE_EMAIL, email);
        intentAction.putExtra(SendMessageReceiver.BUNDLE_EMOTION, emotion);

        final PendingIntent pIntent = PendingIntent.getService(ctx, id, intentAction, PendingIntent.FLAG_UPDATE_CURRENT);

        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ctx)
                .setSmallIcon(R.drawable.ic_emotion_1f44d)
                .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), Emotions.getByKey(emotion)))
                .setContentTitle(ctx.getString(R.string.notification_send_remember_of))
                .setContentText(name)
                .setAutoCancel(false)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_send_white_24dp, ctx.getString(R.string.send_from_notification), pIntent);


        final NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(id, notificationBuilder.build());
    }
}
