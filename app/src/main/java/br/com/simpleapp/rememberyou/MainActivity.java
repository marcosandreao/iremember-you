package br.com.simpleapp.rememberyou;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.AccountPicker;

import br.com.simpleapp.rememberyou.auth.SignInActivity;
import br.com.simpleapp.rememberyou.contacts.ui.ContactsListActivity;
import br.com.simpleapp.rememberyou.gcm.GCMActivity;
import br.com.simpleapp.rememberyou.gcm.QuickstartPreferences;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CHOOSE_ACCOUNT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Title");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //      .setAction("Action", null).show();
                startActivityForResult(new Intent(MainActivity.this, ContactsListActivity.class), REQUEST_CHOOSE_ACCOUNT);
            }
        });

        this.registerGCMIfNeed();
    }

    private void registerGCMIfNeed(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if ( !sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false) ){
            this.startActivity(new Intent(this, GCMActivity.class));
            //final Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
//                    false, null, null, null, null);
  //          startActivityForResult(intent, REQUEST_CHOOSE_ACCOUNT);
        }
        //this.startActivity(new Intent(this, ContactsListActivity.class));
        //this.startActivity(new Intent(this, SignInActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
