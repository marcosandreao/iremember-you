package br.com.simpleapp.rememberyou.service;

import java.util.Date;

import br.com.simpleapp.rememberyou.RememberYouApplication;
import br.com.simpleapp.rememberyou.entity.DaoSession;
import br.com.simpleapp.rememberyou.entity.History;

/**
 * Created by marcos on 29/04/16.
 */
public class HistoryService {

    private final DaoSession session;

    public HistoryService(){
        this.session = RememberYouApplication.instance.daoMaster.newSession();
    }

    public void save(String name, String email, String emotion ) {
        final History history = new History();
        history.setDateTime(new Date());
        history.setEmail(email);;
        history.setName(name);
        history.setEmotion(emotion);
        this.session.getHistoryDao().insert(history);
    }
}
