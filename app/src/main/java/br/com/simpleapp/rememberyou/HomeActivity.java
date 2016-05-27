package br.com.simpleapp.rememberyou;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import br.com.simpleapp.rememberyou.contacts.ui.ContactsListActivity;
import br.com.simpleapp.rememberyou.gcm.QuickstartPreferences;
import br.com.simpleapp.rememberyou.home.HistoryFragment;
import br.com.simpleapp.rememberyou.home.UserFavoriteFragment;
import br.com.simpleapp.rememberyou.utils.DialogUtils;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 5;

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

        final FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryOpenList();
            }
        });
    }

    private void tryOpenList(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            this.openListActivity();
            return;
        }
        final String permission = "android.permission.READ_CONTACTS";

        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                DialogUtils.showRequestPermission(this, new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{permission}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
                });
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {
            this.openListActivity();
        }
    }

    private void openListActivity(){
        this.startActivityForResult(new Intent(HomeActivity.this, ContactsListActivity.class), 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.openListActivity();
                } else {

                }
                return;
            }
        }
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
        int id = item.getItemId();
        this.setFragment(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

}
