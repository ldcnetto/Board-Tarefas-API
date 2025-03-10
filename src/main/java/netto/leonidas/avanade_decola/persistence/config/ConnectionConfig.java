package netto.leonidas.avanade_decola.persistence.config;

import lombok.NoArgsConstructor;
import static lombok.AccessLevel.PRIVATE;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
@NoArgsConstructor(access = PRIVATE)
public class ConnectionConfig {
    @Value("${DATABASE_URL}")
    private String databaseUrl;

    @Value("${DATABASE_USERNAME}")
    private String databaseUsername;

    @Value("${DATABASE_PASSWORD}")
    private String databasePassword;

    public Connection getConnection() throws SQLException {
        var url = databaseUrl ;
        var user = databaseUsername;
        var password = databasePassword;
        var connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(false);
        return connection;
    }
}
