package hei.school.course.dao.mapper;

import hei.school.course.dao.operations.IngredientCrudOperations;
import hei.school.course.endpoint.rest.DishRest;
import hei.school.course.endpoint.rest.IngredientBasicRest;
import hei.school.course.model.Dish;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class DishMapper implements Function<ResultSet, Dish> {
    private final IngredientCrudOperations ingredientCrudOperations;
    @SneakyThrows
    @Override
    public Dish apply(ResultSet resultSet) {
        Dish dish = new Dish();
        Long dishId = resultSet.getLong("id");
        dish.setId(dishId);
        dish.setName(resultSet.getString("name"));
        dish.setDishIngredients(ingredientCrudOperations.findByDishId(dishId));
        dish.setPrice(resultSet.getDouble("price"));
        return dish;
    }
}
