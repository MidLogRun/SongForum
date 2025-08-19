package spotify;

import http.server.externalapis.Requester;
import http.server.externalapis.spotify.ApiGetFailed;
import http.server.externalapis.spotify.SpotifyRequestStrategy;
import http.server.externalapis.SpotifyUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class SpotifyUrlTest {

    Requester requester;
    String spotifyUrl = "https://api.spotify.com/v1";

    @BeforeEach
    public void setup() {
        requester = new Requester(new SpotifyRequestStrategy());
    }

    @Test
    public void testBuildAppendIdRequest() throws ApiGetFailed {
        String id = "1PgfRdl3lPyACfUGH4pquG";
        SpotifyUrl url = new SpotifyUrl(spotifyUrl).getAlbum(id);

        HttpResponse<String> response = requester.sendGetRequest(url.buildString());
        System.out.println(response.body());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testBuildGetAlbumTracks() throws ApiGetFailed {
        String id = "1PgfRdl3lPyACfUGH4pquG";
        SpotifyUrl url = new SpotifyUrl(spotifyUrl).getAlbumTracks(id);
        HttpResponse<String> response = requester.sendGetRequest(url.buildString());
        System.out.println(response.body());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testBuildGetTracks() throws ApiGetFailed {
        String id = "11dFghVXANMlKmJXsNCbNl";
        SpotifyUrl url = new SpotifyUrl(spotifyUrl).getTrack(id);
        HttpResponse<String> response = requester.sendGetRequest(url.buildString());
        System.out.println(response.body());
        assertEquals(200, response.statusCode());
    }

}
