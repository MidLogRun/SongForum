package http.server.object_files;

public record ArtistId(String name) {
    @Override
    public String toString() {
        return "ArtistId[" + name + "]";
    }
}
