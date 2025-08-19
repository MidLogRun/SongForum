package database;

import io.github.cdimascio.dotenv.Dotenv;
import org.postgresql.jdbc.PgConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector {
    Logger logger = LoggerFactory.getLogger(Connector.class);
    static Connection connection;
    Dotenv dotenv = Dotenv
            .configure()
            .directory("src/main/resources")
            .load();


    public Connection getConnection() throws SQLException {
        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USER");
        String password = "";

        if (url == null || user == null) {
            String msg = "Database credentials are not properly set!";
            logger.error(msg);
            throw new SQLException(msg);
        }

        try {
            if (connection == null) {
                connection = DriverManager.getConnection(url, user, password);
            }
            logger.info("Connection successful {}", connection.toString());
            return connection;
        } catch (SQLException e) {
            logger.error("Failed to establish database connection: {}", e.getMessage());
            throw e;
        }
    }
}
