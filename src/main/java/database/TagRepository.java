package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TagRepository extends AbstractRepository<String> {
    public TagRepository(Connection connection) {
        super(connection);
    }

    @Override
    void bindInsert(PreparedStatement preparedStatement, String tag) throws SqlBindException {
        try {
            preparedStatement.setString(1, tag);
        } catch (SQLException e) {
            throw new SqlBindException("could not bind insert on tag");
        }
    }

    @Override
    void bindUpdate(PreparedStatement preparedStatement, String tag) throws SQLException {

    }

    @Override
    void bindDelete(PreparedStatement preparedStatement, String tag) throws SQLException, SqlBindException {
        try {
            preparedStatement.setString(1, tag);
        } catch (SQLException e) {
            throw new SqlBindException("could not bind delete on tag");
        }
    }

    @Override
    void doUpdate(String entity) throws NotUpdatedException {
    }

    @Override
    String tableName() {
        return "tag";
    }

    @Override
    String idColumn() {
        return "id";
    }

    @Override
    String insertSql() {
        return "INSERT INTO tag (name) VALUES (?) ON CONFLICT DO NOTHING";
    }

    @Override
    String updateSql() {
        return "";
    }

    @Override
    String deleteSql() {
        return "DELETE FROM tag WHERE name = ?";
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connection;
    }

    public Integer getTagId(String tag) {
        String sql = "SELECT id FROM tag WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, tag);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else throw new NotFoundException("tag not found");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
