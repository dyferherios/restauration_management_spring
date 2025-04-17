package hei.school.course.dao.operations;

import ch.qos.logback.core.recovery.ResilientFileOutputStream;
import hei.school.course.dao.Datasource;
import hei.school.course.dao.mapper.DishAndOrderStatusMapper;
import hei.school.course.dao.mapper.OrderMapper;
import hei.school.course.model.Criteria;
import hei.school.course.model.DishAndOrderStatus;
import hei.school.course.model.Order;
import hei.school.course.model.Status;
import hei.school.course.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.io.PipedReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderCrudOperations  implements CrudOperations<Order>{

    private final Datasource datasource;
    private final OrderMapper orderMapper;
    private final DishAndOrderStatusMapper dishAndOrderStatusMapper;

    @Override
    public List<Order> getAll(int page, int size) {
        return List.of();
    }

    @Override
    public Order findById(Long id) {
        return null;
    }

    @SneakyThrows
    @Override
    public Order findByCriteria(Criteria criteria) {
        String sql = "select o.id, o.order_reference, o.creation_date from orders o where 1=1 and " + "o." + criteria.getKey() + " = ?";
        try(Connection connection = datasource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, (String) criteria.getValue());
            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    Order order = orderMapper.apply(resultSet);
                    order.setStatus(getOrderStatus(order.getId()));
                    return order;
                }
                throw new NotFoundException("Order with " + criteria.getKey() + " : " + criteria.getValue() + " not found");
            }
        }
    }

    @SneakyThrows
    @Override
    public List<Order> saveAll(List<Order> entities) {
        List<Order> orders = new ArrayList<>();
        String sqlWithId = "insert into orders (id, order_reference, creation_date) values (?, ?, ?)"
                + " on conflict (id) do nothing"
                + " returning id, order_reference, creation_date";
        String sqlWithoutId = "insert into orders (order_reference, creation_date) values (?, ?) on conflict (order_reference) do nothing"
                + " returning id, order_reference, creation_date";

        try (Connection connection = datasource.getConnection()){
            PreparedStatement statementWithId = connection.prepareStatement(sqlWithId);
            PreparedStatement statementWithoutId = connection.prepareStatement(sqlWithoutId);

            entities.forEach(order -> {
                PreparedStatement statement;
                int index =1;
                try{
                    if(order.getId()!=null){
                        statement = statementWithId;
                        statement.setLong(index++, order.getId());
                    }else{
                        statement = statementWithoutId;
                    }
                    statement.setString(index++, order.getReference());
                    statement.setObject(index, order.getCreationDate(), java.sql.Types.DATE);
                    try(ResultSet resultSet = statement.executeQuery()){
                        if(resultSet.next()){
                            Order orderSaved = orderMapper.apply(resultSet);
                            orders.add(orderSaved);
                            
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return orders;
    }

    @SneakyThrows
    public List<DishAndOrderStatus> getOrderStatus(Long orderId){
        List<DishAndOrderStatus> dishAndOrderStatusList = new ArrayList<>();
        String sql = "select os.id,os.id_order, os.order_status as status, os.creation_date from order_status os where id_order=?";
        try(Connection connection = datasource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, orderId);
            try(ResultSet resultSet = statement.executeQuery()){
                while(resultSet.next()){
                    DishAndOrderStatus dishAndOrderStatus = dishAndOrderStatusMapper.apply(resultSet);
                    dishAndOrderStatusList.add(dishAndOrderStatus);
                }
            }
        }
        return dishAndOrderStatusList;
    }
    
    @SneakyThrows
    public List<DishAndOrderStatus> saveOrderStatus(Order order){
        List<DishAndOrderStatus> dishAndOrderStatusList = new ArrayList<>();
        String sql = "insert into order_status (id_order, order_status, creation_date) values (?, ?::order_status_process, ?)" +
                " on conflict (id_order, order_status) do nothing returning id, order_status as status, creation_date";
        try(Connection connection = datasource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, order.getId());
            statement.setString(2, String.valueOf(order.getStatus().getFirst().getStatus()));
            statement.setObject(3, order.getCreationDate());
            System.out.println(order.getCreationDate());
            System.out.println(statement);
            System.out.println("passed here");
            try(ResultSet resultset = statement.executeQuery()){
                if(resultset.next()){
                    DishAndOrderStatus dishAndOrderStatus = dishAndOrderStatusMapper.apply(resultset);
                    dishAndOrderStatusList.add(dishAndOrderStatus);
                }
            }
        }
        return dishAndOrderStatusList;
    }
    
}
