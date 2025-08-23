package http.server.json_readers;

import com.fasterxml.jackson.databind.JsonNode;
import http.server.object_files.FmAlbum;
import http.server.object_files.FmArtist;
import http.server.object_files.FmTrack;

import java.util.ArrayList;
import java.util.List;

public class JsonResponseReader {


    public static FmAlbum getAlbum(JsonNode node, FmArtist fmArtist) throws AlbumJsonException {
        JsonNode albumArray = node.get("album");
        if (albumArray == null) {
            throw new AlbumJsonException("No album found");
        }
        return buildAlbum(albumArray, fmArtist);
    }

    private static List<String> getTags(JsonNode node) {
        JsonNode tagsArray = node.get("tag");
        List<String> tags = new ArrayList<>();
        if (tagsArray == null || !tagsArray.isArray()) {
            return tags;
        }



        for (JsonNode tagNode : tagsArray) {
            String name = tagNode.get("name").asText();
            tags.add(name);
        }

        return tags;
    }

    private static List<FmTrack> getTracks(JsonNode node, String albumName) throws AlbumJsonException {
        if (node == null) throw new AlbumJsonException("No track found");
        JsonNode tracksNode = node.get("track");
        if (tracksNode == null || !tracksNode.isArray()) {
            throw new AlbumJsonException("No tracks found");
        }

        List<FmTrack> tracks = new ArrayList<>();
        for (JsonNode trackNode : tracksNode) {
            tracks.add(new FmTrack(
                    trackNode.get("duration").asInt(),
                    trackNode.get("url").asText(),
                    trackNode.get("name").asText(),
                    albumName
            ));
        }
        return tracks;
    }

    public static boolean isValidAlbum(JsonNode node) {
        JsonNode albumArray = node.get("album");
        if (albumArray == null) {
            return false;
        }

        if (albumArray.get("name").asText() == null) return false;
        if (albumArray.get("url").asText() == null) return false;
        if (albumArray.get("listeners").asText() == null) return false;
        List<String> tags = getTags(albumArray.get("tags"));
        if (tags.isEmpty()) return false;
        try {
            getTracks(albumArray.get("tracks"), albumArray.get("name").asText());
        } catch (AlbumJsonException e) {
            return false;
        }

        return true;
    }

    private static FmAlbum buildAlbum(JsonNode albumArrayNode, FmArtist artist) throws AlbumJsonException {
        String title = albumArrayNode.get("name").asText();
        String url = albumArrayNode.get("url").asText();
        Integer listeners = Integer.parseInt(albumArrayNode.get("listeners").asText());

        List<String> tags = getTags(albumArrayNode.get("tags"));
        List<FmTrack> tracks = getTracks(albumArrayNode.get("tracks"), title);

        String summary = "";
        JsonNode wikiNode = albumArrayNode.get("wiki");
        if (wikiNode != null) {
            summary = wikiNode.get("summary").asText();
        }


        return new FmAlbum(artist, title, tags, tracks, url, summary, listeners);
    }


    public static FmTrack getTrack(JsonNode node) throws AlbumJsonException {
        JsonNode trackNode = node.get("track");
        if (trackNode == null) {
            throw new AlbumJsonException("trackNode is null");
        }

        Integer duration = Integer.parseInt(trackNode.get("duration").asText()) / 1000; //divide by 1000 to get seconds
        String url = trackNode.get("url").asText();
        String name = trackNode.get("name").asText();
        String albumName = trackNode.get("album").get("title").asText();

        return new FmTrack(duration, url, name, albumName);
    }

    private static List<String> getSimilarArtists(JsonNode node) {
        JsonNode artistsArray = node.get("artist");
        if (artistsArray == null) {
            return new ArrayList<>();
        }
        List<String> similarArtists = new ArrayList<>();
        for (JsonNode artistNode : artistsArray) {
            similarArtists.add(artistNode.get("name").asText());
        }
        return similarArtists;
    }


    public static FmArtist getArtist(JsonNode node) throws AlbumJsonException {
        JsonNode artistNode = node.get("artist");
        if (artistNode == null) {
            throw new AlbumJsonException("artistNode is null");
        }

        String name = artistNode.get("name").asText();
        String url = artistNode.get("url").asText();
        List<String> tags = getTags(artistNode.get("tags"));
        List<String> similar = getSimilarArtists(artistNode.get("similar"));
        String summary = artistNode.get("bio").get("summary").asText();
        return new FmArtist(name, url, tags, similar, summary);
    }

    public static List<String> extractAlbumNames(JsonNode node) throws AlbumJsonException {
        JsonNode topArray = node.get("topalbums");
        if (topArray == null) {
            throw new AlbumJsonException("topalbums is null");
        }
        JsonNode albumsArray = topArray.get("album");
        if (albumsArray == null) {
            throw new AlbumJsonException("albums is null");
        }
        List<String> albumNames = new ArrayList<>();
        for (JsonNode albumNode : albumsArray) {
            albumNames.add(albumNode.get("name").asText());
        }
        return albumNames;
    }

}
