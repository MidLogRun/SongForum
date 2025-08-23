package http.server.object_files;

import java.util.List;

public record FmArtist(String name,
                       String url,
                       List<String> tags,
                       List<String> similarArtists,
                       String summary) {

    @Override
    public String toString() {
        return "FmArtist[name=" + name + ", url=" + url + "]";
    }
}
