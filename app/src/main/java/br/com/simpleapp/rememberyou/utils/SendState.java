package br.com.simpleapp.rememberyou.utils;

/**
 * Created by socram on 04/05/16.
 */
public enum SendState {

    STATE_START,
    STATE_DONE_SUCCESS,
    STATE_DONE_NEED_INVITE,
    STATE_DONE_ERROR;

    public static SendState getState(int index) {
        return values()[index];
    }
}
