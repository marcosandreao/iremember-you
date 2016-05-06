package br.com.simpleapp.rememberyou.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import br.com.simpleapp.rememberyou.IConstatns;
import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.entity.User;
import br.com.simpleapp.rememberyou.utils.Emotions;
import br.com.simpleapp.rememberyou.utils.SendState;

public class NotificationMessageReceiver extends BroadcastReceiver {

    private UserService service = new UserService();

    public NotificationMessageReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final int state = intent.getIntExtra(SendRemember.EXTRA_STATE, -1);
        final String email = intent.getStringExtra(SendRemember.EXTRA_TO);
        final User user = this.service.findByEmail(email);
        if (user != null){
            final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            String text ="";
            switch (state){
                case SendRemember.STATE_DONE_ERROR:
                    text = "Enviado com sucesso";
                    break;
                case SendRemember.STATE_DONE_SUCCESS:
                    text = "Enviado com sucesso";
                    break;
                case SendRemember.STATE_DONE_NEED_INVITE:
                    text = "Usuário não cadastrado";
                    break;
                case SendRemember.STATE_START:
                    text = "Iniciado";
                    break;
            }
            final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .addAction(R.drawable.ic_send_white_24dp, text, null);

            notificationManager.notify( user.getId().intValue(), notificationBuilder.build());

        }

    }

}
