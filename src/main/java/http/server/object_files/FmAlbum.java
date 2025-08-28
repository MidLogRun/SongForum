package http.server.object_files;

import java.util.List;

public record FmAlbum(FmArtist artist,
                      String title,
                      List<String> tags,
                      List<FmTrack> tracks,
                      String url,
                      String summary,
                      Integer listeners) {

    //Dummy constructor as a DTO
    public FmAlbum(FmArtist artist, String title) {
        this(artist, title, null, null, "", "", null);
    }

    @Override
    public String toString() {
        return "Album[" + title + ", " + artist.name() + "]";
    }
}
