package br.com.simpleapp.rememberyou.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.entity.User;

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

            String text ="";
            switch (state){
                case SendRemember.STATE_DONE_ERROR:
                    text = user.getName() + context.getString(R.string.toast_send_error);
                    break;
                case SendRemember.STATE_DONE_SUCCESS:
                    text = context.getString(R.string.toast_send_success) + user.getName();
                    break;
                case SendRemember.STATE_DONE_NEED_INVITE:
                    text = user.getName() + context.getString(R.string.toast_send_not_found_user);
                    break;
                case SendRemember.STATE_START:
                    text = context.getString(R.string.toast_send_start);
                    break;
            }
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();

        }

    }

}
