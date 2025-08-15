package spotify;

import http.server.externalapis.spotify.SpotifyAuthenticator;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class SpotifyAuthenticatorTest {

    @Test
    public void testSpotifyAuthenticator() {
        SpotifyAuthenticator spotifyAuthenticator = new SpotifyAuthenticator();
        HttpResponse<String> response = spotifyAuthenticator.authenticate();

        System.out.println(response.body());
        System.out.println(response.statusCode());
        assertEquals(200, response.statusCode());
    }
}
