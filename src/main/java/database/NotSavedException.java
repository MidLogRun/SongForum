package database;

import java.sql.SQLException;

public class NotSavedException extends SQLException {
    public NotSavedException(String message) {
        super(message);
    }
}
