package database;

import http.server.object_files.FmArtist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ArtistRepository extends AbstractRepository<FmArtist> {

    public ArtistRepository(Connection connection) {
        super(connection);
    }

    @Override
    void bindInsert(PreparedStatement preparedStatement, FmArtist artist) throws SQLException, SqlBindException {
        try {
            preparedStatement.setString(1, artist.name());
            preparedStatement.setString(2, artist.url());
            preparedStatement.setString(3, artist.summary());
        } catch (SQLException e) {
            throw new SqlBindException("could not bind artist to FmArtist in insert");
        }
    }

    @Override
    void bindUpdate(PreparedStatement preparedStatement, FmArtist artist) throws SQLException {

    }

    @Override
    void bindDelete(PreparedStatement preparedStatement, FmArtist artist) throws SQLException, SqlBindException {
        try {
            preparedStatement.setString(1, artist.name());
        } catch (SQLException e) {
            throw new SqlBindException("could not bind artist to FmArtist in delete");
        }
    }

    @Override
    void doUpdate(FmArtist artist) throws NotUpdatedException {

    }

    @Override
    String tableName() {
        return "artist";
    }

    @Override
    String idColumn() {
        return "id";
    }

    @Override
    String insertSql() {
        return "INSERT INTO artist (name, url, summary) VALUES (?, ?, ?) ON CONFLICT DO NOTHING";
    }

    @Override
    String updateSql() {
        return "";
    }

    @Override
    String deleteSql() {
        return "DELETE FROM artist WHERE name = ?";
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connection;
    }
}
