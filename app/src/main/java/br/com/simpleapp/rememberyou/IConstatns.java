package br.com.simpleapp.rememberyou;

import android.content.IntentFilter;

/**
 * Created by marcos on 04/05/16.
 */
public interface IConstatns {

    String INTENT_FILTER_ACTION_DETAIL = "callback.send.from.DETAIL";

    IntentFilter INTENT_FILTER_DETAIL = new IntentFilter(INTENT_FILTER_ACTION_DETAIL);

}
