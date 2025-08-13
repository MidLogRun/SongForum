package http.server.spotify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.catalina.valves.ExtendedAccessLogValve;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class SpotifyAuthenticator {
    HttpClient client;
    private Dotenv dotenv;
    private String accessToken;
    private long tokenExpirationTime;
    private static Logger logger = LoggerFactory.getLogger(SpotifyAuthenticator.class);

    private static SpotifyAuthenticator authInstance;

    public SpotifyAuthenticator() {
        client = HttpClient.newHttpClient();
        dotenv = Dotenv.configure()
                .directory("src/main/resources")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        accessToken = "";
        tokenExpirationTime = -1;
    }

    public static SpotifyAuthenticator getInstance() {
        if (authInstance == null) {
            authInstance = new SpotifyAuthenticator();
        }
        return authInstance;
    }

    private HttpRequest clientCredentialsRequest() {
        String clientId = dotenv.get("SPOT_ID");
        String clientSecret = dotenv.get("SPOT_SECRET");
        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        String requestBody = "grant_type=client_credentials";

        return HttpRequest.newBuilder()
                .uri(URI.create("https://accounts.spotify.com/api/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic " + encodedCredentials)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
    }

    public HttpResponse<String> authenticate() {
        HttpRequest request = clientCredentialsRequest();

        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getToken() {
        if (accessToken.isEmpty() || System.currentTimeMillis() < tokenExpirationTime) {
            try {
                HttpResponse<String> response = authenticate();
                if (response.statusCode() == 200) {
                    String[] token = parseBodyForToken(response.body());
                    this.accessToken = token[0];
                    this.tokenExpirationTime = Long.parseLong(token[1]);
                }
            } catch (JsonProcessingException e) {
                logger.info("Error while getting token");
                throw new RuntimeException(e);
            }
        }
        return accessToken;
    }

    private String[] parseBodyForToken(String responseBody) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(responseBody);
        JsonNode accessTokenNode = rootNode.get("access_token");
        JsonNode expiresNode = rootNode.get("expires_in");

        return new String[]{accessTokenNode.asText(), expiresNode.asText()};
    }
}
