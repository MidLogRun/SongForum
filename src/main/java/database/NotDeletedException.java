package database;

public class NotDeletedException extends Exception {
    public NotDeletedException(String message) {
        super(message);
    }
}
