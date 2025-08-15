import http.server.externalapis.spotify.ApiGetFailed;
import http.server.externalapis.RequestMaker;
import http.server.externalapis.spotify.LastFmRequestStrategy;
import http.server.externalapis.spotify.SpotifyAuthenticator;
import http.server.externalapis.spotify.SpotifyRequestStrategy;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestMakerTest {

    @Test
    public void Spotify_testSendGetRequestForAlbum() throws ApiGetFailed {
        RequestMaker requestMaker = new RequestMaker(new SpotifyRequestStrategy());
        String url = "https://api.spotify.com/v1/albums/4aawyAB9vmqN3uQ7FjRGTy";
        HttpResponse<String> response = requestMaker.sendGetRequest(url);
        assertEquals(200, response.statusCode());
        System.out.println(response.body());
    }

    @Test
    public void Spotify_testSendGetRequestForArtist() throws ApiGetFailed {
        RequestMaker requestMaker = new RequestMaker(new SpotifyRequestStrategy());
        String url = "https://api.spotify.com/v1/artists/0TnOYISbd1XYRBk9myaseg";
        HttpResponse<String> response = requestMaker.sendGetRequest(url);
        assertEquals(200, response.statusCode());
        System.out.println(response.body());
    }

    @Test
    public void Spotify_testSendGetRequestForTracks() throws ApiGetFailed {
        RequestMaker requestMaker = new RequestMaker(new SpotifyRequestStrategy());
        String url = "https://api.spotify.com/v1/albums/4aawyAB9vmqN3uQ7FjRGTy/tracks";
        HttpResponse<String> response = requestMaker.sendGetRequest(url);
        assertEquals(200, response.statusCode());
        System.out.println(response.body());
    }


    @Test
    public void Spotify_testDeprecatedUrl() throws ApiGetFailed {
        RequestMaker requestMaker = new RequestMaker(new SpotifyRequestStrategy());
        String url = "https://api.spotify.com/v1/audio-features/11dFghVXANMlKmJXsNCbNl";
        HttpResponse<String> response = requestMaker.sendGetRequest(url);
        assertEquals(403, response.statusCode());
        System.out.println(response.body());
    }

    @Test
    public void LastFm_testSendGetRequestForAlbum() throws ApiGetFailed {
        RequestMaker requestMaker = new RequestMaker(new LastFmRequestStrategy());
        String url = "https://ws.audioscrobbler.com/2.0/?method=album.getinfo&artist=Cher&album=Believe";
        HttpResponse<String> response = requestMaker.sendGetRequest(url);
        assertEquals(200, response.statusCode());
        System.out.println(response.body());
    }


}
