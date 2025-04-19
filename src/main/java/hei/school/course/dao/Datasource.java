package hei.school.course.dao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class Datasource {

    private final String user = System.getenv("DB_USER");
    private final String pwd = System.getenv("DB_PASSWORD");
    private final String dbUrl = System.getenv("DB_URL");
    private final String dbHost = System.getenv("DB_HOST");
    private final String dbPort = System.getenv("DB_PORT");
    private final String dbName = System.getenv("DB_NAME");

    private final String jdbcUrl;

    public Datasource() {
        if (dbUrl == null || dbHost == null || dbPort == null || dbName == null) {
            throw new IllegalArgumentException("Erreur : Certaines variables d'environnement sont manquantes.");
        }
        jdbcUrl = String.format("%s%s:%s/%s", dbUrl, dbHost, dbPort, dbName);
        System.out.println(jdbcUrl);
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(jdbcUrl, user, pwd);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion : " + e.getMessage(), e);
        }
    }
}