package ru.otus.hw.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;

@ChangeLog(order = "001")
public class InitChangelog {

    @ChangeSet(order = "001", id="001_drop_database", author = "softi", runAlways = true)
    public void dropDatabase(MongoDatabase db) {
        db.drop();
    }

}
