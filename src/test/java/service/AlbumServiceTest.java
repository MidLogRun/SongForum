package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import database.*;
import http.server.externalapis.LastFmWrapper;
import http.server.externalapis.Requester;
import http.server.externalapis.spotify.ApiGetFailed;
import http.server.externalapis.spotify.LastFmRequestStrategy;
import http.server.json_readers.JsonResponseReaderException;
import http.server.object_files.AlbumId;
import http.server.object_files.FmAlbum;
import http.server.service.AlbumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class AlbumServiceTest {
    AlbumRepository repository;
    AlbumService service;
    LastFmWrapper api;

    @BeforeEach
    public void setUp() throws SQLException {
        repository = new AlbumRepository(Connector.getConnection());
        api = new LastFmWrapper(new Requester(new LastFmRequestStrategy()));
        service = new AlbumService(repository, api);
    }

    public AlbumId identify(FmAlbum album) {
        return new AlbumId(album.title(), album.artist().name());
    }


    @Test
    public void testGetAlbumByNameAlreadyExists() throws JsonResponseReaderException, ApiGetFailed, JsonProcessingException, SQLException, NotDeletedException {
        String albumName = "Baby";
        String artist = "Dijon";
        FmAlbum album = api.getAlbum(artist, albumName);
        repository.insert(album);
        assertTrue(repository.exists(identify(album)));
        AlbumId identifier = identify(album);
        FmAlbum album2 = service.getAlbumByName(identifier);
        assertNotNull(album2);
        repository.delete(album2);
        assertFalse(repository.exists(identify(album)));
    }

    @Test
    public void testGetAlbumNotExists() throws JsonResponseReaderException, ApiGetFailed, JsonProcessingException, NotDeletedException, SQLException {
        AlbumId identifier = new AlbumId("Baby", "Dijon");
        FmAlbum album = service.getAlbumByName(identifier);
        assertTrue(repository.exists(identify(album)));
        repository.delete(album);
        assertFalse(repository.exists(identify(album)));
    }
}
