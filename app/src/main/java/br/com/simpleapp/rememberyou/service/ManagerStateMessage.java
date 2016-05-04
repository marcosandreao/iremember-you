package br.com.simpleapp.rememberyou.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marcos on 04/05/16.
 */
public class ManagerStateMessage {

    private static ManagerStateMessage instance;

    private ManagerStateMessage(){}

    private Map<String, Integer> states = new HashMap<>();

    public static ManagerStateMessage getInstance(){
        if ( instance == null ) {
            instance = new ManagerStateMessage();
        }
        return instance;
    }


    public void setState(String key, Integer state) {
        this.states.put(key, state);
    }

    public void remove(String key) {
        this.states.remove(key);
    }

    public int getState(String to) {
        if ( !this.states.containsKey(to) ) {
            return -1;
        }
        return this.states.get(to);
    }

    /*
    public boolean isSending(String to) {
        return sending;
    }

    public boolean isFinishWithError(String to) {
        return finishWithError;
    }

    public boolean isFinishUserNoexist(String to) {
        return finishUserNoexist;
    }

    public boolean isFinishSuccess(String to) {
        return finishSuccess;
    }
    */
}
