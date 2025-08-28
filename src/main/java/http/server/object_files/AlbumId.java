package http.server.object_files;

public record AlbumId(String title, String artistName) {

    @Override
    public String toString() {
        return "AlbumId[" + artistName + ", " + title + "]";
    }
}
