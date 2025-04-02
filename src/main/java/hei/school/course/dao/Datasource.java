package hei.school.course.dao;

import edu.hei.school.restaurant.service.exception.ServerException;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class Datasource {
    private final String user = System.getenv("db_user");
    private final String pwd = System.getenv("db_password");
    private final String dbUrl = System.getenv("db_url");
    private final String dbHost = System.getenv("db_host");
    private final String dbPort = System.getenv("db_port");
    private final String dbName = System.getenv("db_name");
    private final String jdbcUrl;


    public Datasource() {
        jdbcUrl = dbUrl + dbHost + ":" + dbPort + "/" + dbName;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(jdbcUrl, user, pwd);
        } catch (SQLException e) {
            throw new ServerException(e);
        }
    }
}
