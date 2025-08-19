package database;

import java.io.Serializable;
import java.sql.Connection;

// T is a entity like an FmAlbum, FmTrack
public abstract class Repository<T> implements Table<T> {
    Connection connection;

    public Repository(Connection connection) {
        this.connection = connection;
    }


}
