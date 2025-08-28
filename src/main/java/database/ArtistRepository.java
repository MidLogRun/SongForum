package database;

import http.server.object_files.ArtistId;
import http.server.object_files.FmArtist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArtistRepository extends AbstractRepository<FmArtist, ArtistId> {
    private final TagRepository tagRepository = new TagRepository(this.connection);

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
    public boolean exists(ArtistId id) {
        String sql = "SELECT * FROM artist WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, id.name());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            logger.info("SQLException while checking if artist exists {}", e.getMessage());
            return false;
        }
    }

    private boolean exists(String name) throws SQLException {
        String sql = "SELECT 1 FROM artist WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    private List<String> getArtistTagsByName(String name) throws SQLException {
        String sql = "SELECT tg.name FROM tag tg JOIN artist_tag at ON at.tag_id = tg.id JOIN artist a ON a.id = at.artist_id WHERE a.name = ?";
        List<String> tags = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                tags.add(resultSet.getString("name"));
            }
            return tags;
        }
    }

    private List<String> getSimilarArtistsById(Integer id) throws SQLException {
        String sql = """
                SELECT a.id AS artist_id,
                       a.name AS artist_name,
                       r.id AS related_id,
                       r.name AS related_name
                FROM artist a
                JOIN artist_artist aa ON (aa.artist_id_1 = a.id OR aa.artist_id_2 = a.id)
                JOIN artist r ON r.id = CASE WHEN aa.artist_id_1 = a.id THEN aa.artist_id_2 ELSE aa.artist_id_1 END
                WHERE a.id = ?;
                """;
        List<String> similarArtists = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                similarArtists.add(resultSet.getString("related_name"));
            }
            return similarArtists;
        }
    }

    public FmArtist getArtistByName(String name) throws SQLException {
        String sql = "SELECT * FROM artist WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            preparedStatement.executeQuery();
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String url = resultSet.getString("url");
                String summary = resultSet.getString("summary");
                List<String> tags = getArtistTagsByName(name);
                Integer id = resultSet.getInt("id");
                List<String> similarArtists = getSimilarArtistsById(id);
                return new FmArtist(name, url, tags, similarArtists, summary);
            } else {
                throw new NotFoundException("could not find artist by name " + name);
            }
        } catch (SQLException e) {
            logger.info("SQL exception in getArtistByName " + e.getMessage());
            throw e;
        }
    }

    private boolean isAdjacent(int id1, int id2) throws SQLException {
        String sql = "SELECT * FROM artist_artist WHERE artist_id_1 = ? AND artist_id_2 = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id1);
            preparedStatement.setInt(2, id2);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }


    private void connectSimilar(FmArtist artist) throws NotFoundException, SQLException {
        String sql = "INSERT INTO artist_artist (artist_id_1, artist_id_2) VALUES (?, ?)";
        int id1 = getArtistIdByName(artist.name());
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (String name : artist.similarArtists()) {
                if (exists(name)) {
                    logger.info("Similar artist exists. Connecting {} to {} ", artist.name(), name);

//                    FmArtist similarArtist = getArtistByName(name);
                    int id2 = getArtistIdByName(name);
                    int minId = Math.min(id1, id2);
                    int maxId = Math.max(id1, id2);

                    if (!isAdjacent(minId, maxId)) {
                        preparedStatement.setInt(1, minId);
                        preparedStatement.setInt(2, maxId);
                        preparedStatement.addBatch();
                    }

                } else {
                    logger.info("Artist {} doesn't exist in db", name);
                }
            }
            preparedStatement.executeBatch();
        }
    }

    int getArtistIdByName(String name) throws SQLException {
        String sql = "SELECT id FROM artist WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                throw new NotFoundException("could not find artistIdByName " + name);
            }
        }
    }

    private void joinTags(FmArtist artist) throws SQLException {
        String sql = "INSERT INTO artist_tag VALUES (?, ?) ON CONFLICT DO NOTHING"; //This works
        int albumId = getArtistIdByName(artist.name());
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (String tag : artist.tags()) {
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

    public List<FmArtist> getArtistsByTag(String tagName) throws SQLException {
        String sql = """
                SELECT a.id , a.name, a.url, a.summary
                FROM artist a
                JOIN artist_tag at ON at.artist_id = a.id
                JOIN tag tg ON tg.id = at.tag_id
                WHERE tg.name = ?
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, tagName);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<FmArtist> artists = new ArrayList<>();
            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String url = resultSet.getString("url");
                String summary = resultSet.getString("summary");
                artists.add(new FmArtist(name, url, getArtistTagsByName(name), getSimilarArtistsById(id), summary));
            }
            return artists;
        }
    }


    @Override
    void doInsert(FmArtist artist) throws SQLException {
        super.doInsert(artist);
        if (!artist.tags().isEmpty()) {
            tagRepository.batchInsert(artist.tags());
            joinTags(artist);
        }
        try {
            connectSimilar(artist);
        } catch (NotFoundException e) {
            logger.info("Couldn't find similar artist");
        }
    }

    public void updateAdjacencyList(FmArtist artist) throws SQLException {
        connectSimilar(artist);
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
