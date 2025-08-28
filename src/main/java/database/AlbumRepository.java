package database;

import http.server.object_files.AlbumId;
import http.server.object_files.FmAlbum;
import http.server.object_files.FmArtist;
import http.server.object_files.FmTrack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AlbumRepository extends AbstractRepository<FmAlbum, AlbumId> {

    private final ArtistRepository artistRepository;
    private final TrackRepository trackRepository;
    private final TagRepository tagRepository;

    public AlbumRepository(Connection connection) {
        super(connection);
        artistRepository = new ArtistRepository(connection);
        trackRepository = new TrackRepository(connection);
        tagRepository = new TagRepository(connection);
    }


    @Override
    void bindInsert(PreparedStatement preparedStatement, FmAlbum album) throws SqlBindException {
        try {
            preparedStatement.setString(1, album.artist().name());
            preparedStatement.setString(2, album.title());
            preparedStatement.setInt(3, album.tags().size());
            preparedStatement.setInt(4, album.tracks().size());
            preparedStatement.setString(5, album.url());
            preparedStatement.setString(6, album.summary());
            preparedStatement.setInt(7, album.listeners());
        } catch (SQLException e) {
            throw new SqlBindException("could not bind insert in albumRepository");
        }
    }

    @Override
    void bindUpdate(PreparedStatement preparedStatement, FmAlbum album) throws SQLException {

    }

    @Override
    void bindDelete(PreparedStatement preparedStatement, FmAlbum album) throws SqlBindException {
        try {
            preparedStatement.setString(1, album.artist().name());
            preparedStatement.setString(2, album.title());
        } catch (SQLException e) {
            throw new SqlBindException("could not bind delete in albumRepository");
        }
    }

    @Override
    void doInsert(FmAlbum album) throws SQLException {
        super.doInsert(album);
        artistRepository.doInsert(album.artist());
        trackRepository.batchInsert(album.tracks());
        if (!album.tags().isEmpty()) {
            tagRepository.batchInsert(album.tags());
            joinTags(album);
        }
    }

    private void joinTags(FmAlbum album) throws SQLException {
        String sql = "INSERT INTO album_tag VALUES (?, ?) ON CONFLICT DO NOTHING";
        int albumId = getAlbumId(album);
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (String tag : album.tags()) {
                Integer tagId = tagRepository.getTagId(tag);
                preparedStatement.setInt(1, albumId);
                preparedStatement.setInt(2, tagId);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            logger.info("Could not join tags");
        }
    }


    @Override
    void doUpdate(FmAlbum album) throws NotUpdatedException {

    }

    @Override
    String tableName() {
        return "album";
    }

    @Override
    String idColumn() {
        return "id";
    }

    @Override
    String insertSql() {
        return """
                INSERT INTO album (artist, title, num_tags, num_tracks, url, summary, num_listeners)
                VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING
                """;
    }

    @Override
    String updateSql() {
        return "";
    }

    public int getAlbumId(FmAlbum album) throws SQLException {
        String query = "SELECT id FROM album WHERE artist = ? AND title = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, album.artist().name());
            preparedStatement.setString(2, album.title());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                } else {
                    throw new NotFoundException("album not found");
                }
            }
        }
    }


    @Override
    String deleteSql() {
        return "DELETE FROM album WHERE artist = ? AND title = ?";
    }

    @Override
    public boolean exists(AlbumId id) {
        String sql = "SELECT 1 FROM album WHERE artist = ? AND title = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, id.artistName());
            preparedStatement.setString(2, id.title());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            logger.error("SQLException while checking if album exists", e);
            return false;
        }

    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connection;
    }

    private List<FmTrack> getTracks(String title) throws SQLException {
        return trackRepository.getTracksForAlbum(title);
    }

    private List<String> getTags(Integer albumId) throws SQLException {
        return tagRepository.getAlbumTags(albumId);
    }

    private FmArtist getArtist(String name) throws SQLException {
        return artistRepository.getArtistByName(name);
    }


    public FmAlbum getAlbum(AlbumId id) throws SQLException {
        String query = "SELECT * FROM album WHERE title = ? AND artist = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.title());
            preparedStatement.setString(2, id.artistName());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Integer albumId = resultSet.getInt("id");
                    return new FmAlbum(getArtist(id.artistName()),
                            id.title(),
                            getTags(albumId),
                            getTracks(id.title()),
                            resultSet.getString("url"),
                            resultSet.getString("summary"),
                            resultSet.getInt("num_listeners"));
                } else
                    throw new NotFoundException("album not found");
            } catch (SQLException e) {
                throw new NotFoundException("SQL exception caught in getAlbum" + e.getMessage());
            }
        } catch (SQLException e) {
            throw new NotFoundException("SQL exception caught in getAlbum" + e.getMessage());
        }

    }
}
