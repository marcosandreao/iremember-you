package br.com.simpleapp.rememberyou;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import br.com.simpleapp.rememberyou.contacts.ui.ContactDetailActivity;
import br.com.simpleapp.rememberyou.contacts.ui.ContactsListActivity;
import br.com.simpleapp.rememberyou.entity.User;
import br.com.simpleapp.rememberyou.gcm.QuickstartPreferences;
import br.com.simpleapp.rememberyou.home.UserFavoriteFragment;
import br.com.simpleapp.rememberyou.service.SendRemember;

public class MainActivity extends AppCompatActivity implements UserFavoriteFragment.OnListFragmentInteractionListener {

    private static final int REQUEST_CHOOSE_ACCOUNT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.registerGCMIfNeed();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, ContactsListActivity.class), REQUEST_CHOOSE_ACCOUNT);
            }
        });

        this.getSupportFragmentManager().beginTransaction().replace(R.id.content, UserFavoriteFragment.newInstance(0)).commit();

    }

    private void registerGCMIfNeed(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if ( !sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false) ){
            this.startActivity(new Intent(this, WizardActivity.class));
            this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(User item) {
        Uri contactUri = Uri.parse(item.getContactId());
        Intent intent = new Intent(this, ContactDetailActivity.class);
        intent.setData(contactUri);
        startActivityForResult(intent, REQUEST_CHOOSE_ACCOUNT);
    }

    @Override
    public void onSendInteraction(User mItem) {
        try {
            SendRemember.startActionSend(this.getBaseContext(), mItem.getEmail(), mItem.getLastEmotion());
        } catch (Exception e ) {
            e.printStackTrace();
            Log.e("onSendInteraction", e.getMessage());
        }
    }

}
