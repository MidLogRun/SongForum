package http.server.externalapis;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import http.server.externalapis.spotify.ApiGetFailed;
import http.server.json_readers.JsonResponseReaderException;
import http.server.json_readers.JsonResponseReader;
import http.server.object_files.AlbumId;
import http.server.object_files.FmAlbum;
import http.server.object_files.FmArtist;
import http.server.object_files.FmTrack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LastFmWrapper {
    Logger logger = LoggerFactory.getLogger(LastFmWrapper.class);
    private static final String BASE_URL = "https://ws.audioscrobbler.com/2.0/";
    private final Requester requester;
    private final ObjectMapper mapper = new ObjectMapper();


    public LastFmWrapper(Requester requester) {
        this.requester = requester;
    }

    private String getAlbumResponse(String artist, String album) throws ApiGetFailed {
        LastFmUrl url = new LastFmUrl(BASE_URL).getAlbumInfo(artist, album);
        try {
            return requester.sendGetRequest(url.buildString()).body();
        } catch (ApiGetFailed e) {
            logger.info("ApiGetFailed error in getAlbumResponse: {}", e.getMessage());
            throw e;
        }
    }

    private String getArtistTopAlbumResponse(String artist) throws ApiGetFailed {
        LastFmUrl url = new LastFmUrl(BASE_URL).getArtistTopAlbums(artist);
        try {
            return requester.sendGetRequest(url.buildString()).body();
        } catch (ApiGetFailed e) {
            logger.info("ApiGetFailed error in getAlbumResponse: {}", e.getMessage());
            throw e;
        }
    }

    public FmAlbum getAlbum(AlbumId id) throws ApiGetFailed, JsonResponseReaderException, JsonProcessingException {
        String response = getAlbumResponse(id.artistName(), id.title());
        JsonNode node = mapper.readTree(response);
        FmArtist fmArtist = getArtist(id.artistName());
        return JsonResponseReader.getAlbum(node, fmArtist);
    }

    public FmAlbum getAlbum(String artist, String albumName) throws ApiGetFailed, JsonProcessingException, JsonResponseReaderException {
        String response = getAlbumResponse(artist, albumName);
        JsonNode node = mapper.readTree(response);
        FmArtist fmArtist = getArtist(artist);
        return JsonResponseReader.getAlbum(node, fmArtist);
    }

    private String getTrackResponse(String artist, String trackName) throws ApiGetFailed {
        LastFmUrl url = new LastFmUrl(BASE_URL).getTrackInfo(artist, trackName);
        try {
            return requester.sendGetRequest(url.buildString()).body();
        } catch (ApiGetFailed e) {
            logger.info("ApiGetFailed error in getAlbumResponse: {}", e.getMessage());
            throw e;
        }
    }

    public FmTrack getTrack(String artist, String trackName) throws ApiGetFailed, JsonProcessingException, JsonResponseReaderException {
        String response = getTrackResponse(artist, trackName);
        JsonNode node = mapper.readTree(response);
        return JsonResponseReader.getTrack(node);
    }

    private String getArtistResponse(String artist) throws ApiGetFailed {
        LastFmUrl url = new LastFmUrl(BASE_URL).getArtistInfo(artist);
        try {
            return requester.sendGetRequest(url.buildString()).body();
        } catch (ApiGetFailed e) {
            logger.info("ApiGetFailed error in getArtistResponse: {}", e.getMessage());
            throw e;
        }
    }

    public FmArtist getArtist(String artist) throws ApiGetFailed, JsonProcessingException, JsonResponseReaderException {
        String response = getArtistResponse(artist);
        JsonNode node = mapper.readTree(response);
        return JsonResponseReader.getArtist(node);
    }


    public List<FmArtist> getArtists(List<String> artistNames) throws ApiGetFailed, JsonProcessingException, JsonResponseReaderException {
        List<FmArtist> artists = new ArrayList<>();
        for (String artistName : artistNames) {
            String response = getArtistResponse(artistName);
            JsonNode node = mapper.readTree(response);
            artists.add(JsonResponseReader.getArtist(node));
        }
        return artists;
    }

    public List<FmAlbum> getAllAlbumsByArtist(String artist) throws ApiGetFailed, JsonProcessingException, JsonResponseReaderException {
        List<FmAlbum> albums = new ArrayList<>();
        String response = getArtistTopAlbumResponse(artist);
        JsonNode node = mapper.readTree(response);
        List<String> albumNames = JsonResponseReader.extractAlbumNames(node);
        FmArtist fmArtist = getArtist(artist);

        for (String albumName : albumNames) {
            String albumResponse = getAlbumResponse(artist, albumName);
            JsonNode albumResponseNode = mapper.readTree(albumResponse);
            if (JsonResponseReader.isValidAlbum(albumResponseNode))
                albums.add(JsonResponseReader.getAlbum(albumResponseNode, fmArtist)); // Do I return null from getAlbum()?
        }
        return albums;
    }
}
