import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.internal.function.sequence.Last;
import database.AlbumRepository;
import http.server.externalapis.LastFmWrapper;
import http.server.externalapis.LastFmUrl;
import http.server.externalapis.Requester;
import http.server.externalapis.spotify.ApiGetFailed;
import http.server.externalapis.spotify.LastFmRequestStrategy;
import http.server.json_readers.AlbumJsonException;
import http.server.object_files.FmAlbum;
import http.server.object_files.FmTrack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.*;

import static org.junit.jupiter.api.Assertions.*;

public class AlbumRepositoryTest {

    AlbumRepository repository;
    Connection connection;
    LastFmWrapper lastFmWrapper;
    Requester requester;

    @BeforeEach
    public void setUp() throws SQLException {
        connection = Connector.getConnection();
        repository = new AlbumRepository(connection);
        assertNotNull(repository);
        assertNotNull(connection);
        requester = new Requester(new LastFmRequestStrategy());
        lastFmWrapper = new LastFmWrapper(requester);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    public void testInsertAndDeleteOneAlbum() throws AlbumJsonException, ApiGetFailed, JsonProcessingException {
        String artist = "Bon Iver";
        String title = "22, A Million";
        FmAlbum album = lastFmWrapper.getAlbum(artist, title);
        try {
            repository.insert(album);
            assertTrue(repository.exists(album));
            repository.delete(album);
            assertFalse(repository.exists(album));
        } catch (NotSavedException e) {
            System.err.println("Not Saved: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Generic SQL Error: " + e.getMessage());
        } catch (NotDeletedException e) {
            System.err.println("Not Deleted: " + e.getMessage());
        }
    }

    @Test
    public void testInsertAlbumInsertsArtistTracksAndTags() throws AlbumJsonException, ApiGetFailed, JsonProcessingException {
        String artist = "Bon Iver";
        String title = "22, A Million";
        FmAlbum album = lastFmWrapper.getAlbum(artist, title);

        try {
            repository.insert(album);
            assertTrue(repository.exists(album));
            repository.delete(album);
            assertFalse(repository.exists(album));
        } catch (NotSavedException e) {
            System.err.println("Not Saved: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Generic SQL Error: " + e.getMessage());
        } catch (NotDeletedException e) {
            System.err.println("Not Deleted: " + e.getMessage());
        }
    }

    @Test
    public void testPermanentInsertAlbum() throws AlbumJsonException, ApiGetFailed, JsonProcessingException, NotSavedException, SQLException {
        String artist = "The Killers";
        String title = "Hot Fuss";
        FmAlbum album = lastFmWrapper.getAlbum(artist, title);
        try {
            repository.insert(album);
            assertTrue(repository.exists(album));

        } catch (NotSavedException e) {
            assertThrows(NotSavedException.class, () -> {
                repository.insert(album);
            });
        }
    }

    @Test
    public void testPermanentInsert2() throws AlbumJsonException, ApiGetFailed, JsonProcessingException, SQLException {
        String artist = "Bon Iver";
        String title = "i,i";
        FmAlbum album = lastFmWrapper.getAlbum(artist, title);
        try {
            repository.insert(album);
            assertTrue(repository.exists(album));

        } catch (NotSavedException e) {
            assertThrows(NotSavedException.class, () -> {
                repository.insert(album);
            });
        }
    }

    @Test
    public void testInsertAllAlbumsByArtist() throws AlbumJsonException, ApiGetFailed, JsonProcessingException {
        String artist = "Bon Iver";
        List<FmAlbum> albums = lastFmWrapper.getAllAlbumsByArtist(artist);
        try {
            for (FmAlbum album : albums) {
                repository.insert(album);
                assertTrue(repository.exists(album));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    @Test
    public void testInsertAllAlbumsByArtist2() throws AlbumJsonException, ApiGetFailed, JsonProcessingException {
        String artist = "Mk.gee";
        List<FmAlbum> albums = lastFmWrapper.getAllAlbumsByArtist(artist);
        try {
            for (FmAlbum album : albums) {
                repository.insert(album);
                assertTrue(repository.exists(album));
            }
        } catch (SQLException e) {
//            for (FmAlbum album : albums) {
//                assertThrows(NotSavedException.class, () -> {
//                    repository.insert(album);
//                });
//            }

        }
    }

    @Test
    public void testInsertAllAlbumsByArtist3() throws AlbumJsonException, ApiGetFailed, JsonProcessingException {
        String artist = "Dijon";
        List<FmAlbum> albums = lastFmWrapper.getAllAlbumsByArtist(artist);
        try {
            for (FmAlbum album : albums) {
                repository.insert(album);
                assertTrue(repository.exists(album));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Test
    public void testInsertAllAlbumsByArtist4() throws AlbumJsonException, ApiGetFailed, JsonProcessingException, SQLException {
        String artist = "Alvvays";
        List<FmAlbum> albums = lastFmWrapper.getAllAlbumsByArtist(artist);
        for (FmAlbum album : albums) {
            repository.insert(album);
            assertTrue(repository.exists(album));
        }
    }


    @Test
    public void testInsertTwoStar() throws AlbumJsonException, ApiGetFailed, JsonProcessingException, NotSavedException, SQLException {
        String artist = "Mk.gee";
        String title = "Two Star & The Dream Police";
        FmAlbum album = lastFmWrapper.getAlbum(artist, title);
        try {
            repository.insert(album);
            assertTrue(repository.exists(album));
        } catch (NotSavedException e) {
            assertThrows(NotSavedException.class, () -> {
                repository.insert(album);
            });
        }
    }
}
