package http.server.externalapis.spotify;

public class ApiGetFailed extends Exception {
    public ApiGetFailed(String message) {
        super(message);
    }
}
