package br.com.simpleapp.rememberyou.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

    private static final String EXTRA_TO = "TO";
    private static final String EXTRA_TXT = "TXT";
    private static final String EXTRA_EMOTION = "EMOTION";

    public SendRemember() {
        super("SendRemember");
    }

    public static void startActionSend(Context context, String to, String emotion) {
        Intent intent = new Intent(context, SendRemember.class);
        intent.setAction(ACTION_SEND);
        intent.putExtra(EXTRA_TO, to);
        intent.putExtra(EXTRA_EMOTION, emotion);
        context.startService(intent);
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
                }
            }
        }
    }

    private void handleActionSend(Intent intent) throws IOException {
        final String to = intent.getStringExtra(EXTRA_TO);
        final String emotion = intent.getStringExtra(EXTRA_EMOTION);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());

        final Retrofit retrofit = new Retrofit.Builder().baseUrl( Constants.URL_SERVER).build();

        final IRememberYou service = retrofit.create(IRememberYou.class);
        String from = sharedPreferences.getString(QuickstartPreferences.ACCOUNT, "");
        retrofit.Response<ResponseBody> response =  service.message(from, to, getString(R.string.txt_notification), emotion).execute();

        //TODO: fazer outra forma de feedback
        if ( !response.isSuccess() ) {

        } else {
        }
        Log.d("MESSAGE", response.raw().toString());

    }

}
