package http.server.configurations;

import database.AlbumRepository;
import database.ArtistRepository;
import database.Connector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class DatabaseConfig {

    @Bean
    public Connection connection() throws SQLException {
        return Connector.getConnection();
    }

    @Bean
    public AlbumRepository albumRepository() throws SQLException {
        return new AlbumRepository(connection());
    }

    @Bean
    public ArtistRepository productGroceryRepository() throws SQLException {
        return new ArtistRepository(connection());
    }


}
