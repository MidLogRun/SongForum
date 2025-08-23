import com.fasterxml.jackson.core.JsonProcessingException;
import http.server.externalapis.LastFmWrapper;
import http.server.externalapis.Requester;
import http.server.externalapis.spotify.ApiGetFailed;
import http.server.externalapis.spotify.LastFmRequestStrategy;
import http.server.json_readers.AlbumJsonException;
import http.server.object_files.FmAlbum;
import http.server.object_files.FmArtist;
import http.server.object_files.FmTrack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class FmWrapperTest {
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
        assertEquals(artist.toLowerCase(), album.artist().name().toLowerCase());
        assertEquals(title.toLowerCase(), album.title().toLowerCase());
    }

    @Test
    public void testConvertLastFmTrackResponseToTrack() throws ApiGetFailed, AlbumJsonException, JsonProcessingException {
        String artist = "Bon Iver";
        String title = "beach baby";

        FmTrack track = apiWrapper.getTrack(artist, title);
        assertNotNull(track, "the track object should never be null");
        assertEquals(track.name().toLowerCase(), title.toLowerCase());

        System.out.println("track: " + track);
    }

    @Test
    public void testConvertLastFmArtistResponseToArtist() throws ApiGetFailed, AlbumJsonException, JsonProcessingException {
        String artist = "Bon Iver";
        FmArtist fmArtist = apiWrapper.getArtist(artist);
        assertNotNull(fmArtist, "the artist object should never be null");
        System.out.println(fmArtist);
    }

    @Test
    public void testGetAllAlbumsOfArtist() throws ApiGetFailed, AlbumJsonException, JsonProcessingException {
        String artist = "Bon Iver";
        List<FmAlbum> albums = apiWrapper.getAllAlbumsByArtist(artist);
        assertTrue(albums.size() > 4);
        albums.forEach(System.out::println);
    }

}
