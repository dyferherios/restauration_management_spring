package hei.school.course.dao.operations;

import ch.qos.logback.core.recovery.ResilientFileOutputStream;
import hei.school.course.dao.Datasource;
import hei.school.course.dao.mapper.OrderMapper;
import hei.school.course.model.DishAndOrderStatus;
import hei.school.course.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.io.PipedReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderCrudOperations  implements CrudOperations<Order>{

    private final Datasource datasource;
    private final OrderMapper orderMapper;

    @Override
    public List<Order> getAll(int page, int size) {
        return List.of();
    }

    @Override
    public Order findById(Long id) {
        return null;
    }

    @SneakyThrows
    public Order findByReference(String orderReference) {
        Order order = new Order();
        String sql = "select o.id, o.order_references, o.creation_date from orders o " +
                "where o.order_references=?";
        try(Connection connection = datasource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, orderReference);
            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    order = orderMapper.apply(resultSet);
                }
            }
        }
        return order;
    }

    @Override
    public List<Order> saveAll(List<Order> entities) {
        return List.of();
    }
}
