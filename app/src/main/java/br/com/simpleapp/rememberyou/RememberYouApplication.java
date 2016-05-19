package br.com.simpleapp.rememberyou;

import android.support.multidex.MultiDexApplication;

import br.com.simpleapp.rememberyou.entity.DaoMaster;

/**
 * Created by socram on 23/04/16.
 */
public class RememberYouApplication extends MultiDexApplication {

    public static RememberYouApplication instance = null;

    public DaoMaster daoMaster;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        AnalyticsTrackers.initialize(this);

        final DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(this, "remeber-db", null);
        this.daoMaster = new DaoMaster(openHelper.getWritableDatabase());

    }
}
