package br.com.simpleapp.rememberyou.service;

import java.util.List;

import br.com.simpleapp.rememberyou.RememberYouApplication;
import br.com.simpleapp.rememberyou.entity.DaoSession;
import br.com.simpleapp.rememberyou.entity.User;
import br.com.simpleapp.rememberyou.entity.UserDao;

/**
 * Created by socram on 23/04/16.
 */
public class UserService {

    private final DaoSession session;

    public UserService(){
        this.session = RememberYouApplication.instance.daoMaster.newSession();
    }
    public boolean isFavorie(String email) {
        final UserDao dao = session.getUserDao();
        final List<User> users = dao.queryBuilder().where(UserDao.Properties.Email.eq(email))
                .where(UserDao.Properties.Favorite.eq(true)).limit(1).list();

        return !users.isEmpty();
    }

    public void setWithFavorie(String contactId, String contactName, String emailAddress, String emotion) {
        User user = this.findByEmail(emailAddress);
        if ( user == null ) {
            user = new User();
            user.setEmail(emailAddress);
            user.setName(contactName);
            user.setFavorite(true);
            user.setContactId(contactId);
            user.setLastEmotion(emotion);
            session.getUserDao().insert(user);
        } else {
            user.setContactId(contactId);
            user.setFavorite( user.getFavorite() == null? true : !user.getFavorite() );
            user.setLastEmotion(emotion);
            session.getUserDao().update(user);
        }
    }

    public User findByEmail(String email){

        final List<User> users = session.getUserDao().queryBuilder().where(UserDao.Properties.Email.eq(email)).limit(1).list();
        if ( users.isEmpty() ) {
            return null;
        }
        return users.get(0);
    }

    public long prepareToSent(String contactId, String contactName, String emailAddress, String emotion) {
        User user = this.findByEmail(emailAddress);
        if ( user == null ) {
            user = new User();
            user.setEmail(emailAddress);
            user.setName(contactName);
            user.setLastEmotion(emotion);
            user.setContactId(contactId);
            return session.getUserDao().insert(user);
        }
        user.setContactId(contactId);
        user.setName(contactName);
        user.setLastEmotion(emotion);
        session.getUserDao().update(user);
        return user.getId();
    }

    public List<User> listFavorites() {
        return this.session.getUserDao().queryBuilder().where(UserDao.Properties.Favorite.eq(true)).list();
    }
}
