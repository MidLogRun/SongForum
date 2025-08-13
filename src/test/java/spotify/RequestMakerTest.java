package spotify;

import http.server.spotify.ApiGetFailed;
import http.server.spotify.RequestMaker;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestMakerTest {

    @Test
    public void testSendGetRequestForAlbum() throws ApiGetFailed {
        RequestMaker requestMaker = new RequestMaker();
        String url = "https://api.spotify.com/v1/albums/4aawyAB9vmqN3uQ7FjRGTy";
        HttpResponse<String> response = requestMaker.sendGetRequest(url);
        assertEquals(200, response.statusCode());
        System.out.println(response.body());
    }

    @Test
    public void testSendGetRequestForArtist() throws ApiGetFailed {
        RequestMaker requestMaker = new RequestMaker();
        String url = "https://api.spotify.com/v1/artists/0TnOYISbd1XYRBk9myaseg";
        HttpResponse<String> response = requestMaker.sendGetRequest(url);
        assertEquals(200, response.statusCode());
        System.out.println(response.body());
    }

    @Test
    public void testSendGetRequestForTracks() throws ApiGetFailed {
        RequestMaker requestMaker = new RequestMaker();
        String url = "https://api.spotify.com/v1/albums/4aawyAB9vmqN3uQ7FjRGTy/tracks";
        HttpResponse<String> response = requestMaker.sendGetRequest(url);
        assertEquals(200, response.statusCode());
        System.out.println(response.body());
    }


    @Test
    public void testDeprecatedUrl() throws ApiGetFailed {
        RequestMaker requestMaker = new RequestMaker();
        String url = "https://api.spotify.com/v1/audio-features/11dFghVXANMlKmJXsNCbNl";
        HttpResponse<String> response = requestMaker.sendGetRequest(url);
        assertEquals(403, response.statusCode());
        System.out.println(response.body());
    }
}
