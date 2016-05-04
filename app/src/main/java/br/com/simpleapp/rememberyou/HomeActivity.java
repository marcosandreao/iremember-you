package br.com.simpleapp.rememberyou;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import br.com.simpleapp.rememberyou.gcm.QuickstartPreferences;
import br.com.simpleapp.rememberyou.home.HistoryFragment;
import br.com.simpleapp.rememberyou.home.UserFavoriteFragment;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final IntentFilter filter = IConstatns.INTENT_FILTER_DETAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.registerGCMIfNeed();

        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_home);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.nav_favorite).setChecked(true);
        this.setFragment(R.id.nav_favorite);
    }



    private void registerGCMIfNeed(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if ( !sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false) ){
            this.startActivity(new Intent(this, WizardActivity.class));
            this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        this.setFragment(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(this.receiverSend, this.filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.receiverSend);
    }

    private void setFragment(int id ){
        if (id == R.id.nav_favorite) {
            this.getSupportFragmentManager().beginTransaction().replace(R.id.content, UserFavoriteFragment.newInstance(0)).commit();
        } else if (id == R.id.nav_history) {
            this.getSupportFragmentManager().beginTransaction().replace(R.id.content, new HistoryFragment()).commit();
        } else if (id == R.id.nav_settings) {
            this.startActivity(new Intent(this, SettingsActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public BroadcastReceiver receiverSend = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            invalidateOptionsMenu();
        }
    };
}
