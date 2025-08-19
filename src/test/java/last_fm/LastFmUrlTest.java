package last_fm;

import http.server.externalapis.LastFmUrl;
import http.server.externalapis.Requester;

import http.server.externalapis.spotify.ApiGetFailed;
import http.server.externalapis.spotify.LastFmRequestStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class LastFmUrlTest {
    private String lastFmUrl;
    String baseUrl = "https://ws.audioscrobbler.com/2.0/";
    Requester requester;

    @BeforeEach
    public void setup() {
        requester = new Requester(new LastFmRequestStrategy());
    }

    @Test
    public void testLastFmAlbumTest() throws ApiGetFailed {
        LastFmUrl url = new LastFmUrl(baseUrl).getAlbumInfo("Bon Iver", "22, A Million");
        HttpResponse<String> response = requester.sendGetRequest(url.buildString());
        System.out.println(url.buildString());
        assertEquals(200, response.statusCode());
        System.out.println(response.body());
    }

    @Test
    public void testGetTopAlbumTagsTest() throws ApiGetFailed {
        LastFmUrl url = new LastFmUrl(baseUrl).getTopAlbumTags("Bon Iver", "22, A Million");
        String urlStr = url.buildString();
        HttpResponse<String> response = requester.sendGetRequest(urlStr);
        System.out.println(urlStr);
        assertEquals(200, response.statusCode());
        System.out.println(response.body());
    }

    @Test
    public void testGetTopArtistsTest() throws ApiGetFailed {
        LastFmUrl url = new LastFmUrl(baseUrl).getArtistInfo("Bon Iver");
        String urlStr = url.buildString();
        HttpResponse<String> response = requester.sendGetRequest(urlStr);
        System.out.println(urlStr);
        assertEquals(200, response.statusCode());
        System.out.println(response.body());
    }

    @Test
    public void testGetTopArtistTagsTest() throws ApiGetFailed {
        LastFmUrl url = new LastFmUrl(baseUrl).getTopArtistsTags("Bon Iver");
        String urlStr = url.buildString();
        HttpResponse<String> response = requester.sendGetRequest(urlStr);
        System.out.println(urlStr);
        assertEquals(200, response.statusCode());
        System.out.println(response.body());
    }

    @Test
    public void testGetTrackInfoTest() throws ApiGetFailed {
        LastFmUrl url = new LastFmUrl(baseUrl).getTrackInfo("Bon Iver", "00000 Million");
        String urlStr = url.buildString();
        HttpResponse<String> response = requester.sendGetRequest(urlStr);
        System.out.println(urlStr);
        assertEquals(200, response.statusCode());
        System.out.println(response.body());
    }

    @Test
    public void testSearchTrack() throws ApiGetFailed {
        LastFmUrl url = new LastFmUrl(baseUrl).searchTrack("00000 Million");
        String urlStr = url.buildString();
        HttpResponse<String> response = requester.sendGetRequest(urlStr);
        System.out.println(urlStr);
        assertEquals(200, response.statusCode());
        System.out.println(response.body());
    }


}
