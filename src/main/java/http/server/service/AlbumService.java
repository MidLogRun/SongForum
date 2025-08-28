package http.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import database.AlbumRepository;
import database.NotFoundException;
import http.server.externalapis.LastFmWrapper;
import http.server.externalapis.spotify.ApiGetFailed;
import http.server.json_readers.JsonResponseReaderException;
import http.server.object_files.AlbumId;
import http.server.object_files.FmAlbum;
import http.server.object_files.FmArtist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class AlbumService implements ForumService {
    private AlbumRepository repository;
    private LastFmWrapper api;
    Logger logger = LoggerFactory.getLogger(AlbumService.class);

    @Autowired
    public AlbumService(AlbumRepository repository, LastFmWrapper api) {
        this.repository = repository;
        this.api = api;
    }

    public FmAlbum getAlbumByName(AlbumId identifier) throws NotFoundException, SQLException, ApiGetFailed, JsonResponseReaderException, JsonProcessingException {
        if (repository.exists(identifier)) {
            logger.info("{} exists in repository", identifier);
            return repository.getAlbum(identifier);
        } else {
            logger.info("{} not found in repository. Querying lastFM", identifier);
            FmAlbum album = api.getAlbum(identifier);
            repository.insert(album);
            return repository.getAlbum(identifier);
        }
    }

    public List<FmAlbum> getSimilarAlbums() {
        return null;
    }


}
