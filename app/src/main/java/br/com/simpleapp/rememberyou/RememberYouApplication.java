package br.com.simpleapp.rememberyou;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import br.com.simpleapp.rememberyou.entity.DaoMaster;

/**
 * Created by socram on 23/04/16.
 */
public class RememberYouApplication extends MultiDexApplication {

    public static RememberYouApplication instance = null;

    public DaoMaster daoMaster;

    private static List<String> listFavorites = new ArrayList<>();

    private static final String FAVORITE_KEY = "FAVORITE_KEY";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        AnalyticsTrackers.initialize(this);

        final DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(this, "remeber-db", null);
        this.daoMaster = new DaoMaster(openHelper.getWritableDatabase());

        this.initFavorites();

    }

    private void initFavorites(){
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> favs = sharedPreferences.getStringSet(FAVORITE_KEY, new LinkedHashSet<String>());
        if ( favs.isEmpty() ) {
            favs.addAll(listFavorites);
            sharedPreferences.edit().putStringSet(FAVORITE_KEY, favs).commit();
        } else {
            listFavorites.clear();
            listFavorites.addAll(favs);
        }
    }

    public List<String> listFavorites(){
        return listFavorites;
    }

    public void updateFavorite(String oldFav, String newFav){
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final Set<String> favs = sharedPreferences.getStringSet(FAVORITE_KEY, new LinkedHashSet<String>());
        final List<String> values = new ArrayList<>(favs);
        int index = values.indexOf(oldFav);
        if ( index != -1){
            values.set(index, newFav);
        }
        favs.clear();
        favs.addAll(values);
        sharedPreferences.edit().putStringSet(FAVORITE_KEY, favs).commit();
        listFavorites.clear();
        listFavorites.addAll(favs);
    }

    static {
        final String[] favorites = new String[]{
                "emotions_1f609",
                "rostos_1f494",
                "emotions_1f634",
                "emotions_1f600",
                "emotions_1f60e",
                "emotions_1f60d",
                "gestos_270c",
                "emotions_1f621"
        };

        for ( int i = 0; i < favorites.length; i++ ) {
            listFavorites.add(favorites[i]);
        }
    }

}
