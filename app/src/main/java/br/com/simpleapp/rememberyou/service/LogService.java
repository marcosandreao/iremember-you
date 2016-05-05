package br.com.simpleapp.rememberyou.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.simpleapp.rememberyou.RememberYouApplication;
import br.com.simpleapp.rememberyou.entity.DaoSession;
import br.com.simpleapp.rememberyou.entity.StatusSend;
import br.com.simpleapp.rememberyou.entity.StatusSendDao;
import br.com.simpleapp.rememberyou.entity.User;
import br.com.simpleapp.rememberyou.entity.UserDao;
import br.com.simpleapp.rememberyou.utils.SendState;

/**
 * Created by socram on 04/05/16.
 */
public class LogService {

    private final DaoSession session;

    public LogService(){
        this.session = RememberYouApplication.instance.daoMaster.newSession();
    }

    public long save(String email, SendState state) {
        final StatusSend status = new StatusSend();
        status.setEmail(email);
        status.setState(state.ordinal());
        status.setDateTime(new Date());
        return this.session.getStatusSendDao().insert(status);
    }

    public void update(long id, SendState state) {

        final StatusSend status = this.session.getStatusSendDao().load(id);

        status.setState(state.ordinal());

        this.session.getStatusSendDao().update(status);
    }

    public List<StatusSend> list(){
        final List<StatusSend> result = this.session.getStatusSendDao().queryBuilder().orderDesc(StatusSendDao.Properties.DateTime).list();
        UserDao dao = this.session.getUserDao();
        Map<String, User> users = new HashMap<>();
        StatusSend status;
        User user;
        for ( int i = 0; i < result.size(); i ++) {
            status = result.get(i);
            if ( users.containsKey(status.getEmail())) {
                status.setName(users.get(status.getEmail()).getName());
                continue;
            }

            final List<User> userDb = dao.queryBuilder().where(UserDao.Properties.Email.eq(status.getEmail())).limit(1).list();
            if ( !userDb.isEmpty() ) {
                user = userDb.get(0);
                users.put(status.getEmail(), user);

                status.setName(user.getName());
            }
        }
        return result;
    }


}
