package br.com.simpleapp.rememberyou.service;

import java.util.List;

import br.com.simpleapp.rememberyou.RememberYouApplication;
import br.com.simpleapp.rememberyou.entity.DaoSession;
import br.com.simpleapp.rememberyou.entity.Favorite;
import br.com.simpleapp.rememberyou.entity.FavoriteDao;
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

    public void setWithFavorie(String contactName, String emailAddress) {
        User user = this.findByEmail(emailAddress);
        if ( user == null ) {
            user = new User();
            user.setEmail(emailAddress);
            user.setName(contactName);
            user.setFavorite(true);
            session.getUserDao().insert(user);
        } else {
            user.setFavorite( user.getFavorite() == null? true : !user.getFavorite() );
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
}
