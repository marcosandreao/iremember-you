package br.com.simpleapp.rememberyou.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;

import br.com.simpleapp.rememberyou.AnalyticsTrackers;
import br.com.simpleapp.rememberyou.IConstatns;

public class SendMessageReceiver extends Service {

    public static String BUNDLE_ID = "ID";
    public static String BUNDLE_EMAIL = "EMAIL";
    public static String BUNDLE_EMOTION = "EMOTION";

    public SendMessageReceiver() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("RECEIVER", "SendMessageReceiver onstartComand");

        try {
            AnalyticsTrackers.getInstance().get().setScreenName("SendMessageReceiver");
            AnalyticsTrackers.getInstance().get().send(new HitBuilders.ScreenViewBuilder().build());

            AnalyticsTrackers.getInstance().get().send(new HitBuilders.EventBuilder()
                    .setCategory("Send")
                    .setAction(intent.getStringExtra(BUNDLE_EMOTION))
                    .build());
            SendRemember.startActionSend(this.getBaseContext(), intent.getStringExtra(BUNDLE_EMAIL), intent.getStringExtra(BUNDLE_EMOTION), IConstatns.INTENT_FILTER_ACTION_NOTIFICATION);
        } catch (Exception e ) {
            e.printStackTrace();
            Log.e("RECEIVER", e.getMessage());
        }

        Log.d("RECEIVER", "onstartComand");

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
