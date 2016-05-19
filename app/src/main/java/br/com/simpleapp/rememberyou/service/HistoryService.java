package br.com.simpleapp.rememberyou.service;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.simpleapp.rememberyou.RememberYouApplication;
import br.com.simpleapp.rememberyou.entity.DaoSession;
import br.com.simpleapp.rememberyou.entity.History;
import br.com.simpleapp.rememberyou.entity.HistoryDao;
import br.com.simpleapp.rememberyou.home.dto.HistoryDTO;
import de.greenrobot.dao.query.QueryBuilder;

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
        history.setEmail(email);
        history.setName(name);
        history.setEmotion(emotion);
        this.session.getHistoryDao().insert(history);
    }

    public List<? extends History> list(boolean filterAll) {
        if ( filterAll ) {
            return this.session.getHistoryDao().queryBuilder().list();
        }
        return this.listGrouped();
    }

    public List<? extends History> listGrouped() {

        final Cursor mCursorGroup = this.session.getDatabase().query( HistoryDao.TABLENAME, null, null, null, HistoryDao.Properties.Email.columnName, null, null, null);

        final List<HistoryDTO> items = new ArrayList<>();
        HistoryDTO dto = null;
        if ( mCursorGroup.moveToFirst() ) {
            do {
                final String email = mCursorGroup.getString(mCursorGroup.getColumnIndex(HistoryDao.Properties.Email.columnName));
                final String name = mCursorGroup.getString(mCursorGroup.getColumnIndex(HistoryDao.Properties.Name.columnName));

                dto = new HistoryDTO();
                dto.setName(name);
                dto.setEmail(email);
                dto.setDateTime(new Date());

                final Cursor mCursor = this.session.getDatabase().query(HistoryDao.TABLENAME,
                        new String[]{HistoryDao.Properties.Emotion.columnName}, HistoryDao.Properties.Email.columnName +  " = ?", new String[]{email}, null, null, null, null);


                if (mCursor.moveToFirst()) {
                    do {
                        String emotion = mCursor.getString(mCursor.getColumnIndex(HistoryDao.Properties.Emotion.columnName));
                        if (!dto.countEmotions.containsKey(emotion) ) {
                            dto.countEmotions.put(emotion, 1);
                        } else {
                            dto.countEmotions.put(emotion, dto.countEmotions.get(emotion) + 1 );
                        }

                    } while (mCursor.moveToNext()) ;
                }
                mCursor.close();

                items.add(dto);
            } while (mCursorGroup.moveToNext());
        }
        mCursorGroup.close();
        return items;
    }

}
