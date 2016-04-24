package br.com.simpleapp.rememberyou.utils;

import br.com.simpleapp.rememberyou.R;

/**
 * Created by socram on 23/04/16.
 */
public enum Emotions {
    emotion_1f44d(R.drawable.emotion_1f44d),
    emotion_1f60d(R.drawable.emotion_1f60d),
    emotion_1f494(R.drawable.emotion_1f494),
    emotion_1f600(R.drawable.emotion_1f600),
    emotion_2764(R.drawable.emotion_2764),
    emotion_1f609(R.drawable.emotion_1f609),
    emotion_1f602(R.drawable.emotion_1f602),
    emotion_1f614(R.drawable.emotion_1f614),
    emotion_1f621( R.drawable.emotion_1f621);


    public final int res;

    private Emotions(int res){
        this.res = res;
    }

    public static int getByKey(String key) {
        return valueOf(key).res;
    }
}
