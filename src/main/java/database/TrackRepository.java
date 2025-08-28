package database;

import http.server.object_files.FmTrack;
import http.server.object_files.TrackId;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TrackRepository extends AbstractRepository<FmTrack, TrackId> {

    public TrackRepository(Connection connection) {
        super(connection);
    }

    @Override
    void bindInsert(PreparedStatement preparedStatement, FmTrack track) throws SQLException, SqlBindException {
        try {
            preparedStatement.setInt(1, track.duration());
            preparedStatement.setString(2, track.name());
            preparedStatement.setString(3, track.url());
            preparedStatement.setString(4, track.albumName());
        } catch (SQLException e) {
            throw new SqlBindException("Could not bind insert in track repository");
        }
    }

    @Override
    void bindUpdate(PreparedStatement preparedStatement, FmTrack track) throws SQLException {

    }

    @Override
    void bindDelete(PreparedStatement preparedStatement, FmTrack track) throws SQLException, SqlBindException {
        try {
            preparedStatement.setString(1, track.name());
            preparedStatement.setString(2, track.albumName());
        } catch (SQLException e) {
            throw new SqlBindException("Could not bind insert in track repository");
        }
    }

    @Override
    void doUpdate(FmTrack track) throws NotUpdatedException {

    }

    @Override
    String tableName() {
        return "";
    }

    @Override
    String idColumn() {
        return "";
    }

    @Override
    String insertSql() {
        return """
                INSERT INTO track (duration_s, name, preview_url, album_title) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING
                """;
    }

    @Override
    String updateSql() {
        return "";
    }

    @Override
    String deleteSql() {
        return "DELETE FROM track WHERE name = ? AND album_title = ?";
    }

    @Override
    public boolean exists(TrackId trackId) {
        String sql = "SELECT * FROM track WHERE name  = ? AND  album_title = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, trackId.trackName());
            preparedStatement.setString(2, trackId.albumName());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            logger.info("SQLException in checking Track exists: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connection;
    }

    public List<FmTrack> getTracksForAlbum(String title) throws SQLException {
        String sql = "SELECT * FROM track WHERE album_title = ?";
        List<FmTrack> tracks = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, title);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Integer duration = resultSet.getInt("duration_s");
                    String name = resultSet.getString("name");
                    String previewUrl = resultSet.getString("preview_url");
                    tracks.add(new FmTrack(duration, name, previewUrl, title));
                }
            }
        } catch (SQLException e) {
            throw new NotFoundException("Could not get tracks for album");
        }
        return tracks;
    }
}
