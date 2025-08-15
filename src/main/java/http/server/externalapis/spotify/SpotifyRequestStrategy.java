package http.server.externalapis.spotify;

import http.server.externalapis.RequestStrategy;

import java.net.URI;
import java.net.http.HttpRequest;

public class SpotifyRequestStrategy implements RequestStrategy {

    @Override
    public HttpRequest get(String url) throws ApiGetFailed {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + SpotifyAuthenticator.getInstance().getToken())
                .build();
    }
}
