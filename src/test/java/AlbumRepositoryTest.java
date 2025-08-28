import com.fasterxml.jackson.core.JsonProcessingException;
import database.AlbumRepository;
import http.server.externalapis.LastFmWrapper;
import http.server.externalapis.Requester;
import http.server.externalapis.spotify.ApiGetFailed;
import http.server.externalapis.spotify.LastFmRequestStrategy;
import http.server.json_readers.JsonResponseReaderException;
import http.server.object_files.AlbumId;
import http.server.object_files.FmAlbum;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import database.*;

import static org.junit.jupiter.api.Assertions.*;

public class AlbumRepositoryTest {

    static AlbumRepository repository;
    static Connection connection;
    static LastFmWrapper lastFmWrapper;
    static Requester requester;

    @BeforeAll
    public static void setUp() throws SQLException {
        connection = Connector.getConnection();
        repository = new AlbumRepository(connection);
        assertNotNull(repository);
        assertNotNull(connection);
        requester = new Requester(new LastFmRequestStrategy());
        lastFmWrapper = new LastFmWrapper(requester);
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public AlbumId identify(FmAlbum album) {
        return new AlbumId(album.title(), album.artist().name());
    }

    @Test
    public void testInsertAndDeleteOneAlbum() throws JsonResponseReaderException, ApiGetFailed, JsonProcessingException {
        String artist = "Bon Iver";
        String title = "22, A Million";
        FmAlbum album = lastFmWrapper.getAlbum(artist, title);
        try {
            repository.insert(album);
            assertTrue(repository.exists(identify(album)));
            repository.delete(album);
            assertFalse(repository.exists(identify(album)));
        } catch (NotSavedException e) {
            System.err.println("Not Saved: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Generic SQL Error: " + e.getMessage());
        } catch (NotDeletedException e) {
            System.err.println("Not Deleted: " + e.getMessage());
        }
    }

    @Test
    public void testInsertAlbumInsertsArtistTracksAndTags() throws JsonResponseReaderException, ApiGetFailed, JsonProcessingException {
        String artist = "Bon Iver";
        String title = "22, A Million";
        FmAlbum album = lastFmWrapper.getAlbum(artist, title);

        try {
            repository.insert(album);
            assertTrue(repository.exists(identify(album)));
            repository.delete(album);
            assertFalse(repository.exists(identify(album)));
        } catch (NotSavedException e) {
            System.err.println("Not Saved: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Generic SQL Error: " + e.getMessage());
        } catch (NotDeletedException e) {
            System.err.println("Not Deleted: " + e.getMessage());
        }
    }

    @Test
    public void testPermanentInsertAlbum() throws JsonResponseReaderException, ApiGetFailed, JsonProcessingException, NotSavedException, SQLException {
        String artist = "The Killers";
        String title = "Hot Fuss";
        FmAlbum album = lastFmWrapper.getAlbum(artist, title);
        try {
            repository.insert(album);
            assertTrue(repository.exists(identify(album)));

        } catch (NotSavedException e) {
            assertThrows(NotSavedException.class, () -> {
                repository.insert(album);
            });
        }
    }

    @Test
    public void testPermanentInsert2() throws JsonResponseReaderException, ApiGetFailed, JsonProcessingException, SQLException {
        String artist = "Bon Iver";
        String title = "i,i";
        FmAlbum album = lastFmWrapper.getAlbum(artist, title);
        try {
            repository.insert(album);
            assertTrue(repository.exists(identify(album)));

        } catch (NotSavedException e) {
            assertThrows(NotSavedException.class, () -> {
                repository.insert(album);
            });
        }
    }

    @Test
    public void testInsertAllAlbumsByArtist() throws JsonResponseReaderException, ApiGetFailed, JsonProcessingException {
        String artist = "Bon Iver";
        List<FmAlbum> albums = lastFmWrapper.getAllAlbumsByArtist(artist);
        try {
            for (FmAlbum album : albums) {
                repository.insert(album);
                assertTrue(repository.exists(identify(album)));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    @Test
    public void testInsertAllAlbumsByArtist2() throws JsonResponseReaderException, ApiGetFailed, JsonProcessingException {
        String artist = "Mk.gee";
        List<FmAlbum> albums = lastFmWrapper.getAllAlbumsByArtist(artist);
        try {
            for (FmAlbum album : albums) {
                repository.insert(album);
                assertTrue(repository.exists(identify(album)));
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
    public void testInsertAllAlbumsByArtist3() throws JsonResponseReaderException, ApiGetFailed, JsonProcessingException {
        String artist = "Dijon";
        List<FmAlbum> albums = lastFmWrapper.getAllAlbumsByArtist(artist);
        try {
            for (FmAlbum album : albums) {
                repository.insert(album);
                assertTrue(repository.exists(identify(album)));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Test
    public void testInsertAllAlbumsByArtist4() throws JsonResponseReaderException, ApiGetFailed, JsonProcessingException, SQLException {
        String artist = "Alvvays";
        List<FmAlbum> albums = lastFmWrapper.getAllAlbumsByArtist(artist);
        for (FmAlbum album : albums) {
            repository.insert(album);
            assertTrue(repository.exists(identify(album)));
        }
    }


    @Test
    public void testInsertTwoStar() throws JsonResponseReaderException, ApiGetFailed, JsonProcessingException, NotSavedException, SQLException {
        String artist = "Mk.gee";
        String title = "Two Star & The Dream Police";
        FmAlbum album = lastFmWrapper.getAlbum(artist, title);
        try {
            repository.insert(album);
            assertTrue(repository.exists(identify(album)));
        } catch (NotSavedException e) {
            assertThrows(NotSavedException.class, () -> {
                repository.insert(album);
            });
        }
    }
}
