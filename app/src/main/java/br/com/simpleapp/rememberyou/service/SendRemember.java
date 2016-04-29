package br.com.simpleapp.rememberyou.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.api.IRememberYou;
import br.com.simpleapp.rememberyou.gcm.QuickstartPreferences;
import br.com.simpleapp.rememberyou.utils.Constants;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class SendRemember extends IntentService {

    private static final String ACTION_SEND = "br.com.simpleapp.rememberyou.service.action.SEND";

    private static final String SEND_BROADCAST_TO = "BROADCAST_TO";

    private static final String EXTRA_TO = "TO";
    private static final String EXTRA_TXT = "TXT";
    private static final String EXTRA_EMOTION = "EMOTION";

    public static final String EXTRA_STATE = "STATE";
    public static final int STATE_START = 1;
    public static final int STATE_DONE_SUCCESS = 2;
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

                try {
                    this.handleActionSend(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                    sendBroadcast(intent, STATE_DONE_ERROR);
                }
            }
        }
    }

    private void handleActionSend(Intent intent) throws IOException {
        final String to = intent.getStringExtra(EXTRA_TO);
        final String emotion = intent.getStringExtra(EXTRA_EMOTION);

        this.sendBroadcast(intent, STATE_START);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());

        final Retrofit retrofit = new Retrofit.Builder().baseUrl( Constants.URL_SERVER).build();

        final IRememberYou service = retrofit.create(IRememberYou.class);
        final String from = sharedPreferences.getString(QuickstartPreferences.ACCOUNT, "");
        final retrofit.Response<ResponseBody> response =  service.message(from, to, getString(R.string.txt_notification), emotion).execute();

        if ( response.isSuccess() ) {
            this.sendBroadcast(intent, STATE_DONE_SUCCESS);
        } else {
            this.sendBroadcast(intent, STATE_DONE_ERROR);
        }
        Log.d("MESSAGE", response.raw().toString());

    }

    private void sendBroadcast(Intent intent, int state) {

        if ( !intent.hasExtra(SEND_BROADCAST_TO) ) {
            return;
        }

        final Intent intentStatus = new Intent(intent.getStringExtra(SEND_BROADCAST_TO));
        intentStatus.putExtra(EXTRA_STATE, state );
        LocalBroadcastManager.getInstance(this.getBaseContext()).sendBroadcast(intentStatus);
    }

}
