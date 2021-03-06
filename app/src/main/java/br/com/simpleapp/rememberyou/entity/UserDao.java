package br.com.simpleapp.rememberyou.entity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import br.com.simpleapp.rememberyou.entity.User;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "USER".
*/
public class UserDao extends AbstractDao<User, Long> {

    public static final String TABLENAME = "USER";

    /**
     * Properties of entity User.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Email = new Property(2, String.class, "email", false, "EMAIL");
        public final static Property ImgUrl = new Property(3, String.class, "imgUrl", false, "IMG_URL");
        public final static Property Favorite = new Property(4, Boolean.class, "favorite", false, "FAVORITE");
        public final static Property LastEmotion = new Property(5, String.class, "lastEmotion", false, "LAST_EMOTION");
        public final static Property ContactId = new Property(6, String.class, "contactId", false, "CONTACT_ID");
    };


    public UserDao(DaoConfig config) {
        super(config);
    }
    
    public UserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"NAME\" TEXT," + // 1: name
                "\"EMAIL\" TEXT NOT NULL ," + // 2: email
                "\"IMG_URL\" TEXT," + // 3: imgUrl
                "\"FAVORITE\" INTEGER," + // 4: favorite
                "\"LAST_EMOTION\" TEXT," + // 5: lastEmotion
                "\"CONTACT_ID\" TEXT);"); // 6: contactId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USER\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, User entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
        stmt.bindString(3, entity.getEmail());
 
        String imgUrl = entity.getImgUrl();
        if (imgUrl != null) {
            stmt.bindString(4, imgUrl);
        }
 
        Boolean favorite = entity.getFavorite();
        if (favorite != null) {
            stmt.bindLong(5, favorite ? 1L: 0L);
        }
 
        String lastEmotion = entity.getLastEmotion();
        if (lastEmotion != null) {
            stmt.bindString(6, lastEmotion);
        }
 
        String contactId = entity.getContactId();
        if (contactId != null) {
            stmt.bindString(7, contactId);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public User readEntity(Cursor cursor, int offset) {
        User entity = new User( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.getString(offset + 2), // email
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // imgUrl
            cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0, // favorite
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // lastEmotion
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // contactId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, User entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setEmail(cursor.getString(offset + 2));
        entity.setImgUrl(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setFavorite(cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0);
        entity.setLastEmotion(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setContactId(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(User entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(User entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
