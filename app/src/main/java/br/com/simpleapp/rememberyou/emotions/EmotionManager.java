package br.com.simpleapp.rememberyou.emotions;

import android.content.Context;

import java.io.IOException;

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

    private static String[] favorites = new String[]{
            "emotions_1f609",
            "rostos_1f494",
            "emotions_1f634",
            "emotions_1f600",
            "emotions_1f609",
            "emotions_1f60d",
            "gestos_270c",
            "emotions_1f621"
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

    public String[] listFavories(){
        return favorites;
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
