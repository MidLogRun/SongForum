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
        str = str.replace("%", "%25");
        str = str.replace(" ", "%20"); //replace spaces with %20 for urls
        str = str.replace("!", "%21");
        str = str.replace("\"", "%22");
        str = str.replace("#", "%23");
        str = str.replace("$", "%24");
        str = str.replace("&", "%26");
        str = str.replace("\'", "%27");
        str = str.replace("(", "%28");
        str = str.replace(")", "%29");
        str = str.replace("*", "%2A");
        str = str.replace("+", "%2B");
        str = str.replace(",", "%2C");
        str = str.replace("/", "%2F");
        str = str.replace(":", "%3A");
        str = str.replace(";", "%3B");
        str = str.replace("<", "%3C");
        str = str.replace("=", "%3D");
        str = str.replace(">", "%3E");
        str = str.replace("?", "%3F");
        str = str.replace("@", "%40");
        str = str.replace("[", "%5B");
        str = str.replace("\\", "%5C");
        str = str.replace("]", "%5D");
        str = str.replace("^", "%5E");
        str = str.replace("`", "%60");
        str = str.replace("{", "%7B");
        str = str.replace("|", "%7C");
        str = str.replace("}", "%7D");

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

    public LastFmUrl getArtistTopAlbums(String artist) {
        artist = cleanString(artist);
        appendQueryParam("method", "artist.gettopalbums");
        appendQueryParam("artist", artist);
        return this;
    }
}
