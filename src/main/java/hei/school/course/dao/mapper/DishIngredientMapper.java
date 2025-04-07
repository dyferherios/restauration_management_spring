package hei.school.course.dao.mapper;

import hei.school.course.dao.operations.IngredientCrudOperations;
import hei.school.course.endpoint.rest.DishIngredientRest;
import hei.school.course.endpoint.rest.IngredientBasicRest;
import hei.school.course.model.DishIngredient;
import hei.school.course.model.Unit;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class DishIngredientMapper implements Function<ResultSet, DishIngredient> {

    @SneakyThrows
    @Override
    public DishIngredient apply(ResultSet resultSet) {
       DishIngredient dishIngredient = new DishIngredient();
       dishIngredient.setId(resultSet.getLong("id"));
       dishIngredient.setUnit(Unit.valueOf(resultSet.getString("unit")));
       dishIngredient.setRequiredQuantity(resultSet.getDouble("required_quantity"));
       return dishIngredient;
    }
}
