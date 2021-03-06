package br.com.simpleapp.rememberyou.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.squareup.okhttp.ResponseBody;

import br.com.simpleapp.rememberyou.IConstatns;
import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.api.IRememberYou;
import br.com.simpleapp.rememberyou.gcm.QuickstartPreferences;
import br.com.simpleapp.rememberyou.utils.Constants;
import br.com.simpleapp.rememberyou.utils.SendState;
import retrofit.Retrofit;


public class SendRemember extends IntentService {

    private static final String ACTION_SEND = "br.com.simpleapp.rememberyou.service.action.SEND";

    private static final String SEND_BROADCAST_TO = "BROADCAST_TO";

    public static final String EXTRA_TO = "TO";
    public static final String EXTRA_EMOTION = "EMOTION";

    public static final String EXTRA_STATE = "STATE";
    public static final int STATE_START = 0;
    public static final int STATE_DONE_SUCCESS = 1;
    public static final int STATE_DONE_NEED_INVITE = 2;
    public static final int STATE_DONE_ERROR = 3;



    public SendRemember() {
        super("SendRemember");
    }

    public static void startActionSend(Context context, String to, String emotion, String broadcastCallback) {
        Intent intent = new Intent(context, SendRemember.class);
        intent.setAction(ACTION_SEND);
        intent.putExtra(EXTRA_TO, to);
        intent.putExtra(EXTRA_EMOTION, emotion);
        if ( broadcastCallback != null ) {
            intent.putExtra(SEND_BROADCAST_TO, broadcastCallback);
        }
        context.startService(intent);
    }
    public static void startActionSend(Context context, String to, String emotion) {
        startActionSend(context, to, emotion, null);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("RECEIVER", "SendRemember onHandleIntent");

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND.equals(action)) {
               this.handleActionSend(intent);
            }
        }
    }

    private void handleActionSend(Intent intent) {
        try {

            final String to = intent.getStringExtra(EXTRA_TO);

            final String emotion = intent.getStringExtra(EXTRA_EMOTION);

            this.sendBroadcast(intent, to, STATE_START);

            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());

            final Retrofit retrofit = new Retrofit.Builder().baseUrl( Constants.URL_SERVER).build();

            final IRememberYou service = retrofit.create(IRememberYou.class);
            final String from = sharedPreferences.getString(QuickstartPreferences.TOKEN, "");
            final String name = sharedPreferences.getString(QuickstartPreferences.NICK_NAME, "");
            final retrofit.Response<ResponseBody> response =
                    service.message(from, to, emotion, name).execute();

            if ( response.isSuccess() ) {
                this.sendBroadcast(intent, to, STATE_DONE_SUCCESS);
            } else {
                if (response.code() == 404 ) {
                    this.sendBroadcast(intent, to, STATE_DONE_NEED_INVITE);
                } else {
                    this.sendBroadcast(intent, to, STATE_DONE_ERROR);
                }
            }
            Log.d("MESSAGE", response.raw().toString());
        } catch (Exception e) {
            final String to = intent.getStringExtra(EXTRA_TO);
            this.sendBroadcast(intent, to, STATE_DONE_ERROR);
            e.printStackTrace();
        }

    }

    private void sendBroadcast(Intent intent, String to, int state) {

        ManagerStateMessage.getInstance().setState(to, state);

        if ( !intent.hasExtra(SEND_BROADCAST_TO) ) {
            return;
        }
        final String targetBroadcast = intent.getStringExtra(SEND_BROADCAST_TO);

        final Intent intentStatus = new Intent(targetBroadcast);
        intentStatus.putExtra(EXTRA_STATE, state );
        intentStatus.putExtra(EXTRA_TO, to );
        if (IConstatns.INTENT_FILTER_ACTION_NOTIFICATION.equals(targetBroadcast)) {
            this.getBaseContext().sendBroadcast(intentStatus);
        } else {
            LocalBroadcastManager.getInstance(this.getBaseContext()).sendBroadcast(intentStatus);
        }

    }

}
