package br.com.simpleapp.rememberyou.emotions;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.simpleapp.rememberyou.RememberYouApplication;

/**
 * Created by socram on 22/05/16.
 */
public class EmotionManager {

    private static EmotionManager instance;

    private static final String BASE_URI_PNG = "file:///android_asset/%s/%s.png";

    private static final String BASE_URI = "file:///android_asset/%s/%s";

    private static final String[] categories = new String[]{
            "emotions",
            "gestos",
            "rostos",
            "comidas",
            "animais",
            "esporte",
            "construcoes",
            "escola_trabalho",
            "parque",
            "placas",
            "bandeiras",
            "outros"
    };

    private EmotionManager(){}

    public static EmotionManager getInstance(){
        if ( instance == null ) {
            instance = new EmotionManager();
        }
        return instance;
    }

    public String[] getCategories(){
        return categories;
    }

    public String[] listBycategories(String cat, Context context){
        try {
            return context.getAssets().list(cat);
        } catch (IOException e) {
            return new String[0];
        }
    }

    public List<String> listFavorites(){
        return RememberYouApplication.instance.listFavorites();
    }

    public void updateFavorites(String oldFav, String newFav) {
        RememberYouApplication.instance.updateFavorite(oldFav, newFav);
    }

    public String buildUri(String cat, String emotion){
        String baseUri = BASE_URI_PNG;
        if ( emotion.endsWith(".png") ) {
            baseUri = BASE_URI;
        }
        return String.format(baseUri, cat, emotion);
    }

    public String buildUri(String emotion){
        final String values[] = emotion.split("_");
        return String.format(BASE_URI_PNG, values[0], values[1]);
    }

    public String buildFile(String emotion) {
        final String values[] = emotion.split("_");
        return values[0] + "/" + values[1] + ".png";
    }
}
