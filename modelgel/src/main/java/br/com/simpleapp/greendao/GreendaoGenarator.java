package br.com.simpleapp.greendao;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class GreendaoGenarator {
    public static void main(String[] args) throws Exception {
        final Schema schema = new Schema(1000, "br.com.simpleapp.rememberyou.entity");

        GreendaoGenarator.createUser(schema);

        new DaoGenerator().generateAll(schema, "app/src/main/java/");
    }

    private static void createUser(Schema schema) {
        final Entity user = schema.addEntity("User");
        user.addIdProperty();
        user.addStringProperty("name");
        user.addStringProperty("email").notNull();
        user.addStringProperty("imgUrl");
        user.addBooleanProperty("favorite");
        user.addStringProperty("lastEmotion");
        user.addStringProperty("contactId");
    }
}
