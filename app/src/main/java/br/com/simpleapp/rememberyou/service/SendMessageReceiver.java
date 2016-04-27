package br.com.simpleapp.rememberyou.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class SendMessageReceiver extends Service {

    public static String BUNDLE_ID = "ID";
    public static String BUNDLE_EMAIL = "EMAIL";
    public static String BUNDLE_EMOTION = "EMOTION";

    public SendMessageReceiver() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("RECEIVER", "SendMessageReceiver onstartComand");

        SendRemember.startActionSend(this.getBaseContext(), intent.getStringExtra(BUNDLE_EMAIL), intent.getStringExtra(BUNDLE_EMOTION));

        Log.d("RECEIVER", "onstartComand");

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
