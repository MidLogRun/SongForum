package http.server.object_files;

public record FmTrack(Integer duration, String url, String name, String albumName) {

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FmTrack [name=")
                .append(name)
                .append(", url=")
                .append(url)
                .append(", duration=")
                .append(duration)
                .append(", album=")
                .append(albumName);
        return builder.toString();
    }
}
