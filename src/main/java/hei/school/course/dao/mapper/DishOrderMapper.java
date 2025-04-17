package hei.school.course.dao.mapper;

import hei.school.course.dao.operations.DishCrudOperations;
import hei.school.course.model.DishOrder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class DishOrderMapper implements Function<ResultSet, DishOrder> {
    @SneakyThrows
    @Override
    public DishOrder apply(ResultSet resultSet) {
        DishOrder dishOrder = new DishOrder();
        dishOrder.setId(resultSet.getLong("id_dish_order"));
        dishOrder.setQuantity(resultSet.getDouble("quantity"));
        return dishOrder;
    }
}
