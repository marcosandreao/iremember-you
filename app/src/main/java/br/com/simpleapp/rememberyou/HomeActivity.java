package br.com.simpleapp.rememberyou;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.simpleapp.rememberyou.entity.StatusSend;
import br.com.simpleapp.rememberyou.gcm.QuickstartPreferences;
import br.com.simpleapp.rememberyou.home.HistoryFragment;
import br.com.simpleapp.rememberyou.home.UserFavoriteFragment;
import br.com.simpleapp.rememberyou.service.LogService;
import br.com.simpleapp.rememberyou.utils.SendState;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final IntentFilter filter = IConstatns.INTENT_FILTER_DETAIL;
    private ListPopupWindow listPopupWindow;
    private HomeSpinnerAdapter adapterDowpdown;
    private final LogService service = new LogService();
    private Long count;

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


    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        this.loadLog();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.loadLog();
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

    private void loadLog(){
        new LogLoader().execute();
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

    private void createPopupWindow(View view){
        if ( this.listPopupWindow == null ) {
            this.listPopupWindow = new ListPopupWindow(this);
            this.adapterDowpdown = new HomeSpinnerAdapter(this, this.service.list());
            this.listPopupWindow.setAdapter(this.adapterDowpdown);
            this.listPopupWindow.setAnchorView(view);
            this.listPopupWindow.setWidth(400);
            // listPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            this.listPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            this.listPopupWindow.show();
        } else {
            this.listPopupWindow = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ( item.getItemId() == R.id.item_feedback ) {
            this.createPopupWindow(findViewById(item.getItemId()));
        }
        return super.onOptionsItemSelected(item);
    }

    public static class HomeSpinnerAdapter extends  ArrayAdapter<StatusSend> {

        private LayoutInflater inflater;

        public HomeSpinnerAdapter(Context context, List<StatusSend> objects) {
            super(context, 0, objects);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if ( convertView == null ) {
                convertView = this.inflater.inflate(R.layout.home_dropdown_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            StatusSend status = this.getItem(position);
            holder.tv.setText(status.getName());

            SendState state = SendState.getState(status.getState());
            switch (state) {
                case STATE_DONE_ERROR:
                case STATE_DONE_NEED_INVITE:
                    holder.iv.setImageResource(R.drawable.ic_cloud_off_white_24dp);
                    break;
                case STATE_DONE_SUCCESS:
                    holder.iv.setImageResource(R.drawable.ic_cloud_done_white_24dp);
            }

            return convertView;
        }

        private static class ViewHolder {
            final TextView tv;
            final ImageView iv;

            public ViewHolder(View view) {
                this.tv = (TextView) view.findViewById(android.R.id.text1);
                this.iv = (ImageView) view.findViewById(R.id.iv_status);
            }
        }
    }
    public BroadcastReceiver receiverSend = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            invalidateOptionsMenu();
            HomeActivity.this.loadLog();
        }
    };

    public class LogLoader extends AsyncTask<Void, Void, Long> {

        private LogService service = new LogService();

        @Override
        protected Long doInBackground(Void... params) {
            return this.service.count();
        }

        @Override
        protected void onPostExecute(Long count) {
            HomeActivity.this.count = count;
        }
    }
}
