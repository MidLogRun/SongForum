package http.server.externalapis.spotify;

import http.server.externalapis.RequestStrategy;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.net.http.HttpRequest;

public class LastFmRequestStrategy implements RequestStrategy {
    private final String apiKey;

    public LastFmRequestStrategy() {
        Dotenv dotenv = Dotenv.configure()
                .directory("src/main/resources")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
        this.apiKey = dotenv.get("LAST_FM_KEY");

    }

    @Override
    public HttpRequest get(String url) throws ApiGetFailed {
        return HttpRequest.newBuilder()
                .uri(URI.create(url + "&api_key=" + apiKey + "&format=json"))
                .GET()
                .build();
    }

}
