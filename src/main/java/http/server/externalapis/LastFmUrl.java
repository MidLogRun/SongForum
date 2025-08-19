package http.server.externalapis;

import io.github.cdimascio.dotenv.Dotenv;

public class LastFmUrl extends ExternalApiUrl {
    private String authString;

    public LastFmUrl(String baseUrl) {
        super(baseUrl);
        Dotenv dotenv = Dotenv
                .configure()
                .directory("src/main/resources")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
        authString = dotenv.get("LAST_FM_KEY");
    }

    private String cleanString(String str) {
        char[] chars = str.toCharArray();
        for (int i = 1; i < chars.length; i++) {
            if (chars[i - 1] == 0) {
                chars[i] = Character.toUpperCase(chars[i]);
            }
        }

        str = new String(chars);
        str = str.replace(" ", "%20"); //replace spaces with %20 for urls
        return str;
    }

    public LastFmUrl getAlbumInfo(String artist, String title) {
        artist = cleanString(artist);
        title = cleanString(title);
        appendQueryParam("method", "album.getinfo");
        appendQueryParam("artist", artist);
        appendQueryParam("album", title);
        return this;
    }


    public LastFmUrl getTopAlbumTags(String artist, String album) {
        artist = cleanString(artist);
        album = cleanString(album);
        appendQueryParam("method", "album.gettoptags");
        appendQueryParam("artist", artist);
        appendQueryParam("album", album);
        return this;
    }

    @Override
    public String buildString() {
        appendQueryParam("format", "json");
        appendQueryParam("api_key", authString);
        return baseUrl + queryParams.toString();
    }

    @Override
    public String toString() {
        appendQueryParam("format", "json");
        appendQueryParam("api_key", authString);
        return baseUrl + queryParams.toString();
    }

    public LastFmUrl getArtistInfo(String artist) {
        artist = cleanString(artist);
        appendQueryParam("method", "artist.getinfo");
        appendQueryParam("artist", artist);
        return this;
    }

    public LastFmUrl getTopArtistsTags(String artist) {
        artist = cleanString(artist);
        appendQueryParam("method", "artist.gettoptags");
        appendQueryParam("artist", artist);
        return this;
    }

    public LastFmUrl getTrackInfo(String artist, String track) {
        artist = cleanString(artist);
        track = cleanString(track);
        appendQueryParam("method", "track.getinfo");
        appendQueryParam("track", track);
        appendQueryParam("artist", artist);
        return this;
    }

    public LastFmUrl searchTrack(String track) {
        track = cleanString(track);
        appendQueryParam("method", "track.search");
        appendQueryParam("track", track);
        return this;
    }
}
