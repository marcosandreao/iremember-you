package br.com.simpleapp.rememberyou.utils;

import android.content.Context;
import android.content.Intent;

import br.com.simpleapp.rememberyou.R;

/**
 * Created by marcos on 29/05/16.
 */
public class IntentUtils {

    public static void invite(Context ctx, String email){
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType("text/plain");
        //emailIntent.setType("vnd.android.cursor.item/email");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {email});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ctx.getString(R.string.app_name));
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, ctx.getString(R.string.invite_msg_text) );
        ctx.startActivity(Intent.createChooser(emailIntent, ctx.getString(R.string.invite_msg_chooser)));
    }
}
