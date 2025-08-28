package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface Table<T, ID> {
    Connection getConnection() throws SQLException;

    boolean exists(ID id);

    void insert(T entity) throws SQLException, NotSavedException;

    void update(T entity) throws SQLException, NotUpdatedException;

    void delete(T entity) throws SQLException, NotDeletedException;

    default <R> List<R> loadChildren(String selectSql, Function<ResultSet, R> rowMapper, Object... params) throws SQLException {
        Connection connection = getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSql)) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<R> results = new ArrayList<>();
                while (resultSet.next()) {
                    results.add(rowMapper.apply(resultSet));
                }
                return results;
            }
        }
    }

    default <E> void prepareBatch(String insertSql, BiConsumer<PreparedStatement, E> binder, Collection<E> entities) throws SQLException {
        Connection connection = getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            for (E entity : entities) {
                binder.accept(preparedStatement, entity);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }
    }


}
