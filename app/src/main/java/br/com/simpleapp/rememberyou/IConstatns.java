package br.com.simpleapp.rememberyou;

import android.content.IntentFilter;

/**
 * Created by marcos on 04/05/16.
 */
public interface IConstatns {

    String INTENT_FILTER_ACTION_DETAIL = "callback.send.from.DETAIL";

    String INTENT_FILTER_ACTION_HOME = "callback.send.from.HOME";

    String INTENT_FILTER_ACTION_NOTIFICATION = "callback.send.from.NOTIFICATION";

    IntentFilter INTENT_FILTER_DETAIL = new IntentFilter(INTENT_FILTER_ACTION_DETAIL);

    IntentFilter INTENT_FILTER_HOME = new IntentFilter(INTENT_FILTER_ACTION_HOME);

    IntentFilter INTENT_FILTER_NOTIFICATION = new IntentFilter(INTENT_FILTER_ACTION_NOTIFICATION);

}
