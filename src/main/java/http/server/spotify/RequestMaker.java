package http.server.spotify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class RequestMaker {
    Logger logger = LoggerFactory.getLogger(RequestMaker.class);
    private final HttpClient httpClient;

    public RequestMaker() {
        this.httpClient = HttpClient.newBuilder().build();
    }

    private HttpRequest makeGetRequest(String url) {
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
            HttpRequest request = makeGetRequest(url);
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
