package database;

import http.server.object_files.FmAlbum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AlbumRepository extends AbstractRepository<FmAlbum> {

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
    public boolean exists(FmAlbum album) throws SQLException {
        String sql = "SELECT 1 FROM album WHERE artist = ? AND title = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, album.artist().name());
            preparedStatement.setString(2, album.title());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
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
        tagRepository.batchInsert(album.tags());
        joinTags(album);
    }

    private void joinTags(FmAlbum album) throws SQLException {
        String sql = "INSERT INTO album_tag VALUES (?, ?)";
        int albumId = getAlbumById(album);
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
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
    }

    @Override
    String updateSql() {
        return "";
    }

    public int getAlbumById(FmAlbum album) throws SQLException {
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
    public Connection getConnection() throws SQLException {
        return this.connection;
    }
}
