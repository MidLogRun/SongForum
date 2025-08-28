package http.server.object_files;

public record TrackId(String trackName, String albumName) {
    @Override
    public String toString() {
        return "TrackId[" + trackName + "," + albumName + "]";
    }
}
