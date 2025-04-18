package hei.school.course.dao;

import com.google.api.client.util.Value;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class Datasource {
    String dbUrl = System.getenv("DB_URL");
    String user = System.getenv("DB_USER");
    String password = System.getenv("DB_PASSWORD");

    public Connection getConnection() throws SQLException {
        if (dbUrl == null || user == null || password == null) {
            throw new IllegalStateException("Database environment variables not set!");
        }
        return DriverManager.getConnection(dbUrl, user, password);
    }
}