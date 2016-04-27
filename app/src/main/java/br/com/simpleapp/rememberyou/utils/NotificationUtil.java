package br.com.simpleapp.rememberyou.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;

import br.com.simpleapp.rememberyou.MainActivity;
import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.service.SendMessageReceiver;

/**
 * Created by socram on 26/04/16.
 */
public class NotificationUtil {

    public static void pinNotification(final Context ctx,final int id, final String email, final String name, final String emotion) {

        new DecodeResourseToBitmap(ctx, new IDecodeResourseToBitmap(){

            @Override
            public void onFinish(Bitmap mBitmap) {
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
        }).execute(emotion);

    }

    public static boolean hasNotificationPinned(Context ctx, long id){
        final Intent notificationIntent = new Intent(ctx, SendMessageReceiver.class);
        final PendingIntent test = PendingIntent.getService(ctx, (int) id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return test != null;
    }

    public static void removeNotification(Context context, long id) {
        final Intent notificationIntent = new Intent(context, SendMessageReceiver.class);
        final PendingIntent test = PendingIntent.getService(context, (int) id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if ( test != null ) {
            test.cancel();
        }
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel((int)id);
    }

    public static class DecodeResourseToBitmap extends AsyncTask<String, Void, Bitmap> {

        private IDecodeResourseToBitmap listener;

        private Context mContext;

        public DecodeResourseToBitmap(Context mContext, IDecodeResourseToBitmap listener) {
            this.listener = listener;
            this.mContext = mContext;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return BitmapFactory.decodeResource(this.mContext.getResources(), Emotions.getByKey(params[0]));
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            this.listener.onFinish(bitmap);
        }
    }

    public static interface IDecodeResourseToBitmap {
        void onFinish(Bitmap mBitmap);
    }
}
