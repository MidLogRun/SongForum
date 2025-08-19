package http.server.externalapis;


public class SpotifyUrl extends ExternalApiUrl {

    public SpotifyUrl(String baseUrl) {
        super(baseUrl);
    }

    public SpotifyUrl appendId(String id) {
        appendValue("/" + id);
        return this;
    }

    public SpotifyUrl getAlbum(String albumId) {
        String url = "/albums/" + albumId;
        appendValue(url);
        return this;
    }

    public SpotifyUrl getAlbumTracks(String albumId) {
        SpotifyUrl url = getAlbum(albumId);
        url.appendValue("/tracks");
        return url;
    }

    public SpotifyUrl getTrack(String id) {
        String url = "/tracks/" + id;
        appendValue(url);
        return this;
    }
}
