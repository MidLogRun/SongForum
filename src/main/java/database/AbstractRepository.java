package database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

// T is a entity like an FmAlbum, FmTrack
public abstract class AbstractRepository<T> implements Table<T> {
    Logger logger = LoggerFactory.getLogger(AbstractRepository.class);
    Connection connection;

    public AbstractRepository(Connection connection) {
        this.connection = connection;
    }

    abstract void bindInsert(PreparedStatement preparedStatement, T entity) throws SQLException, SqlBindException;

    abstract void bindUpdate(PreparedStatement preparedStatement, T entity) throws SQLException;

    abstract void bindDelete(PreparedStatement preparedStatement, T entity) throws SQLException, SqlBindException;

    abstract void doUpdate(T entity) throws NotUpdatedException;

    void doDelete(T entity) throws SQLException {
        String sql = deleteSql();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            bindDelete(preparedStatement, entity);
            preparedStatement.executeUpdate();
        } catch (SqlBindException e) {
            throw new RuntimeException(e);
        }
    }

    void doInsert(T entity) throws SQLException {
        String sql = insertSql();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            bindInsert(preparedStatement, entity);
            preparedStatement.executeUpdate();
        } catch (SqlBindException e) {
            throw new RuntimeException(e);
        }
    }

    abstract String tableName();

    abstract String idColumn();

    abstract String insertSql();

    abstract String updateSql();

    abstract String deleteSql();

    @Override
    public boolean exists(T entity) throws SQLException {
        return false;
    }

    @Override
    public void insert(T entity) throws SQLException, NotSavedException {
        connection.setAutoCommit(false);
        try {
            if (exists(entity)) {
                logger.info("Not saved.");
                throw new NotSavedException(entity + " already exists. No insertion");
            }
            logger.info("Inserting");
            doInsert(entity);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            logger.info("Could not insert into {}. Rolling back", tableName());
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    //TODO: quite a useless method. How is anyone supposed to update an entity. with what?
    public void update(T entity) throws SQLException, NotUpdatedException {
        connection.setAutoCommit(false);
        try {
            if (!exists(entity)) {
                logger.info("Not updated.");
                throw new NotUpdatedException(entity + " does not exist.");
            }
            logger.info("Updating.");
            doUpdate(entity);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void delete(T entity) throws SQLException, NotDeletedException {
        connection.setAutoCommit(false);
        try {
            if (!exists(entity)) {
                logger.info("Not deleted.");
                throw new NotDeletedException(entity + " does not exist in the database");
            }
            logger.info("Deleting.");
            doDelete(entity);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            logger.info("Can't delete from {}. Rolling back", tableName(), e);
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void batchInsert(Collection<T> entities) throws SQLException {
        try {
            prepareBatch(insertSql(),
                    (preparedStatement, entity) -> {
                        try {
                            bindInsert(preparedStatement, entity);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        } catch (SqlBindException e) {
                            throw new RuntimeException(e);
                        }

                    }, entities);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
