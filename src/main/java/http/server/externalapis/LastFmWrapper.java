package http.server.externalapis;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import http.server.externalapis.spotify.ApiGetFailed;
import http.server.json_readers.AlbumJsonException;
import http.server.json_readers.JsonResponseReader;
import http.server.object_files.FmAlbum;
import http.server.object_files.FmTrack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public FmAlbum getAlbum(String artist, String albumName) throws ApiGetFailed, JsonProcessingException, AlbumJsonException {
        String response = getAlbumResponse(artist, albumName);
        JsonNode node = mapper.readTree(response);
        return JsonResponseReader.getAlbum(node);
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

    public FmTrack getTrack(String artist, String trackName) throws ApiGetFailed, JsonProcessingException, AlbumJsonException {
        String response = getTrackResponse(artist, trackName);
        JsonNode node = mapper.readTree(response);
        return JsonResponseReader.getTrack(node);
    }


}
