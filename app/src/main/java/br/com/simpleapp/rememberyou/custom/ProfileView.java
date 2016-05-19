package br.com.simpleapp.rememberyou.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.gcm.QuickstartPreferences;

public class ProfileView extends LinearLayout {


    public ProfileView(Context context) {
        super(context);
    }

    public ProfileView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProfileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if ( !this.isInEditMode() ) {
            this.bindProfileInfo();
        }
    }

    private void bindProfileInfo(){
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        final TextView tvName = (TextView) this.findViewById(R.id.tv_name);
        final TextView tvEmail = (TextView) this.findViewById(R.id.tv_email);

        tvName.setText(sharedPreferences.getString(QuickstartPreferences.NICK_NAME, ""));
        tvEmail.setText(sharedPreferences.getString(QuickstartPreferences.ACCOUNT, ""));
    }
}

