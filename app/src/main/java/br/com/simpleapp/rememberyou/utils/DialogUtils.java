package br.com.simpleapp.rememberyou.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.analytics.HitBuilders;

import br.com.simpleapp.rememberyou.AnalyticsTrackers;
import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.gcm.QuickstartPreferences;

/**
 * Created by socram on 06/05/16.
 */
public class DialogUtils {
    private DialogUtils(){}

    public static void showLocationDialogNeedInvite(final Context ctx, String name, final String email, final DialogInterface.OnClickListener callback ) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        if ( !sharedPreferences.getBoolean(QuickstartPreferences.SHOW_DIALOG_INVITE, true) ) {
            callback.onClick(null, 0);
            return;
        }

        final String appname = ctx.getString(R.string.app_name);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(ctx.getString(R.string.dialog_title_user_notfound, appname));
        builder.setMessage(ctx.getString(R.string.dialog_messe_need_invite, name));


        String negativeText = ctx.getString(R.string.hot_show_again_msg_feedback);
        builder.setNeutralButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPreferences.edit().putBoolean (QuickstartPreferences.SHOW_DIALOG_INVITE, false).commit();
                        callback.onClick(dialog, which);
                    }
                });

        String positiveText = ctx.getString(R.string.popup_close_and_exit);
        builder.setNegativeButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onClick(dialog, which);
                    }
                });

        builder.setPositiveButton(R.string.invite_btn,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        IntentUtils.invite(ctx, email);
                        AnalyticsTrackers.getInstance().get().send(new HitBuilders.EventBuilder()
                                .setCategory("Invite")
                                .setAction("Convidar")
                                .build());
                    }
                });


        //builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
        //    @Override
        //    public void onCancel(DialogInterface dialog) {

       //     }
       // });


        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    public static void showLocationDialogError(Context ctx, String name,
                                               final DialogInterface.OnClickListener callbackClose, final DialogInterface.OnClickListener callbackTryagain ) {

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        if ( !sharedPreferences.getBoolean(QuickstartPreferences.SHOW_DIALOG_ERROR, true) ) {
            callbackClose.onClick(null, 0);
            return;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(ctx.getString(R.string.dialog_error_title, name));
        builder.setMessage(ctx.getString(R.string.dialog_error_msg));


        String negativeText = ctx.getString(R.string.hot_show_again_msg_feedback);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPreferences.edit().putBoolean (QuickstartPreferences.SHOW_DIALOG_ERROR, false).commit();
                        callbackClose.onClick(dialog, which);
                    }
                });

        String neutrarTxt = ctx.getString(R.string.dialog_error_tryagain);
        builder.setNeutralButton(neutrarTxt,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callbackTryagain.onClick(dialog, which);
                    }
                });
        String positiveText = ctx.getString(R.string.popup_close_and_exit);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callbackClose.onClick(dialog, which);
                    }
                });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                callbackClose.onClick(dialog, 0);
            }
        });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    public static void showRequestPermission(Context ctx, final DialogInterface.OnClickListener callbackClose ) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(ctx.getString(R.string.request_permission));

        builder.setNegativeButton(ctx.getString(R.string.request_permission_cacell), null);

        builder.setNeutralButton(ctx.getString(R.string.request_permission_ok), callbackClose);

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }


}
