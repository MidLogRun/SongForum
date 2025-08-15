package http.server.externalapis;

import http.server.externalapis.spotify.ApiGetFailed;
import http.server.externalapis.spotify.SpotifyAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RequestMaker {
    Logger logger = LoggerFactory.getLogger(RequestMaker.class);
    private final HttpClient httpClient;

    RequestStrategy strategy;

    public RequestMaker(RequestStrategy RequestStrategy) {
        this.httpClient = HttpClient.newBuilder().build();
        this.strategy = RequestStrategy;
    }

    private HttpRequest spotifyGetRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + SpotifyAuthenticator.getInstance().getToken())
                .build();
    }


    public HttpResponse<String> sendGetRequest(String url) throws ApiGetFailed {
        String msg = "";
        try {
            HttpRequest request = strategy.get(url);
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            msg = "IO error while sending GET request";
        } catch (InterruptedException e) {
            msg = "Interrupted while sending GET request";
        }
        logger.info(msg);
        throw new ApiGetFailed(msg);

    }
}
