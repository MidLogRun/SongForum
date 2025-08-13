package http.server.spotify;

public class ApiGetFailed extends Exception {
    public ApiGetFailed(String message) {
        super(message);
    }
}
