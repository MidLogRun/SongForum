package http.server.json_readers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import http.server.object_files.FmAlbum;
import http.server.object_files.FmTrack;

import java.util.ArrayList;
import java.util.List;

public class JsonAlbumReader {


    public static FmAlbum getAlbum(JsonNode node) throws AlbumJsonException {
        JsonNode albumArray = node.get("album");
        if (albumArray == null) {
            throw new AlbumJsonException("No album found");
        }
        return buildAlbum(albumArray);
    }

    private static List<String> getTags(JsonNode node) throws AlbumJsonException {
        JsonNode tagsArray = node.get("tag");
        if (tagsArray == null) {
            throw new AlbumJsonException("No tags found");
        }

        List<String> tags = new ArrayList<>();
        for (JsonNode tagNode : tagsArray) {
            String name = tagNode.get("name").asText();
            tags.add(name);
        }

        return tags;
    }

    private static List<FmTrack> getTracks(JsonNode node) throws AlbumJsonException {
        JsonNode tracksArray = node.get("track");
        if (tracksArray == null) {
            throw new AlbumJsonException("No tracks found");
        }

        List<FmTrack> tracks = new ArrayList<>();
        for (JsonNode trackNode : tracksArray) {
            tracks.add(new FmTrack(
                    trackNode.get("duration").asInt(),
                    trackNode.get("url").asText(),
                    trackNode.get("name").asText()
            ));
        }
        return tracks;
    }

    private static FmAlbum buildAlbum(JsonNode album) throws AlbumJsonException {
        String artist = album.get("artist").asText();
        String title = album.get("name").asText();
        String url = album.get("url").asText();
        Integer listeners = Integer.parseInt(album.get("listeners").asText());

        List<String> tags = getTags(album.get("tags"));
        List<FmTrack> tracks = getTracks(album.get("tracks"));

        JsonNode wikiNode = album.get("wiki");
        String summary = wikiNode.get("summary").asText();

        return new FmAlbum(artist, title, tags, tracks, url, summary, listeners);
    }

}
