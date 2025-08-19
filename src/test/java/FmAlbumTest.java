import com.fasterxml.jackson.core.JsonProcessingException;
import http.server.externalapis.LastFmWrapper;
import http.server.externalapis.LastFmUrl;
import http.server.externalapis.Requester;
import http.server.externalapis.spotify.ApiGetFailed;
import http.server.externalapis.spotify.LastFmRequestStrategy;
import http.server.json_readers.AlbumJsonException;
import http.server.object_files.FmAlbum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;


public class FmAlbumTest {
    //Only the last fm api should be responsible for constructing album objects
    Requester requester;
    String baseUrl = "https://ws.audioscrobbler.com/2.0/";
    LastFmWrapper apiWrapper;

    @BeforeEach
    public void setup() {
        requester = new Requester(new LastFmRequestStrategy());
        apiWrapper = new LastFmWrapper(requester);
    }


    @Test
    public void testConvertLastFmAlbumResponseToAlbum() throws ApiGetFailed, AlbumJsonException, JsonProcessingException {
        String artist = "Bon Iver";
        String title = "22, A Million";
        FmAlbum album = apiWrapper.getAlbum("Bon Iver", "22, A Million");

        assertNotNull(album, "The album object should never be null unless a json processing error occurred.");
        assertEquals(artist.toLowerCase(), album.artist().toLowerCase());
        assertEquals(title.toLowerCase(), album.title().toLowerCase());

    }

    @Test
    public void test() {
    }
}
