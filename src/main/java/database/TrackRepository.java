package database;

import http.server.object_files.FmTrack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TrackRepository extends AbstractRepository<FmTrack> {

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
    public Connection getConnection() throws SQLException {
        return this.connection;
    }
}
