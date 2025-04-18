package hei.school.course.dao;

import com.google.api.client.util.Value;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class Datasource {
    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    public Connection getConnection() throws SQLException {
        System.out.println("Connecting to: " + dbUrl.replace(password, "****"));
        return DriverManager.getConnection(dbUrl, user, password);
    }
}