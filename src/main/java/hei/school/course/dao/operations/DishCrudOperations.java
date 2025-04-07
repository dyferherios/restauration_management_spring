package hei.school.course.dao.operations;

import hei.school.course.dao.Datasource;
import hei.school.course.dao.mapper.DishMapper;
import hei.school.course.model.Dish;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DishCrudOperations implements CrudOperations<Dish> {
    private final Datasource datasource;
    private final DishMapper dishMapper;

    @Override
    public List<Dish> getAll(int page, int size) {
        List<Dish> dishes = new ArrayList<>();
        try (Connection connection = datasource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "select d.id, d.name, d.price from dish d order by d.id asc limit ? offset ?")) {
            statement.setInt(1, size);
            statement.setInt(2, size * (page - 1));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    dishes.add(dishMapper.apply(resultSet));
                }
                return dishes;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Dish findById(Long id) {
        return null;
    }

    @Override
    public List<Dish> saveAll(List<Dish> entities) {
        return List.of();
    }
}
