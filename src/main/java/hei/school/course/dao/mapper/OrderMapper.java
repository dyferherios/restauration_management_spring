package hei.school.course.dao.mapper;

import hei.school.course.dao.operations.DishCrudOperations;
import hei.school.course.model.DishAndOrderStatus;
import hei.school.course.model.DishOrder;
import hei.school.course.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class OrderMapper implements Function<ResultSet, Order> {
    private final DishCrudOperations dishCrudOperations;

    @SneakyThrows
    @Override
    public Order apply(ResultSet resultSet) {
        Order order = new Order();
        Long orderId = resultSet.getLong("id");
        order.setId(orderId);
        order.setReference(resultSet.getString("order_reference"));
        List<DishOrder> dishOrderList = dishCrudOperations.findByOrderId(orderId);
        order.setDishOrders(dishOrderList);
        Optional<DishOrder> dishOrderWithMinStatus = dishOrderList.stream()
                .filter(dishOrder -> dishOrder.getStatus() != null)
                .min(Comparator.comparingInt(dishOrder -> dishOrder.getStatus().size()))
                .stream().findFirst();
        order.setCreationDate(resultSet.getTimestamp("creation_date"));
        //dishOrderWithMinStatus.ifPresent(dishOrder -> order.setStatus(dishOrder.getStatus()));
        return order;
    }
}
