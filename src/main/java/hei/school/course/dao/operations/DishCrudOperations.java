package hei.school.course.dao.operations;

import hei.school.course.dao.Datasource;
import hei.school.course.dao.mapper.DishAndOrderStatusMapper;
import hei.school.course.dao.mapper.DishMapper;
import hei.school.course.dao.mapper.DishOrderMapper;
import hei.school.course.model.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
    private final IngredientCrudOperations ingredientCrudOperations;
    private final DishOrderMapper dishOrderMapper;
    private final DishAndOrderStatusMapper dishAndOrderStatusMapper;

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
        try (Connection connection = datasource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select d.id, d.name, d.price from dish d where d.id = ?")) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return dishMapper.apply(resultSet);
                }
            }
            throw new RuntimeException("Dish.id=" + id + " not found");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Dish> saveAll(List<Dish> entities) {
        List<Dish> dishes = new ArrayList<>();
        String sqlWithId = "insert into dish (id, name, price) values (?, ?, ?)"
                + " on conflict (id) do update set name=excluded.name, price=excluded.price"
                + " returning id, name, price";
        String sqlWithOutId = "insert into dish (name, price) values (?, ?)"
                + " on conflict (name) do update set price=excluded.price"
                + " returning id, name, price";
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statementWithId = connection.prepareStatement(sqlWithId);
            PreparedStatement statementWithOutId = connection.prepareStatement(sqlWithOutId);
            entities.forEach(entityToSave -> {
                PreparedStatement statement;
                int index = 1;
                if(entityToSave.getId()!=null){
                    statement = statementWithId;
                    try {
                        statement.setLong(index++, entityToSave.getId());
                        statement.setString(index++, entityToSave.getName());
                        statement.setDouble(index, entityToSave.getPrice());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    statement = statementWithOutId;
                    try {
                        statement.setString(index++, entityToSave.getName());
                        statement.setDouble(index, entityToSave.getPrice());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                try(ResultSet resultSet = statement.executeQuery()){
                    if(resultSet.next()){
                        List<DishIngredient> dishIngredients;
                        Dish dish = dishMapper.apply(resultSet);
                        if(!entityToSave.getDishIngredients().isEmpty()){
                            dishIngredients = ingredientCrudOperations.saveDishIngredient(entityToSave);
                            dish.setDishIngredients(dishIngredients);
                        }
                        dishes.add(dish);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dishes;
    }

    @SneakyThrows
    public List<DishOrder> findByOrderId(Long orderId){
        List<DishOrder> dishOrders = new ArrayList<>();
        String sql = "select d.id, d.name, d.price, d_o.id as id_dish_order, d_o.id_dish, d_o.id_order, d_o.quantity " +
                "from dish d join dish_order d_o on d.id = d_o.id_dish where d_o.id_order = ?";
        try(Connection connection = datasource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, orderId);
            try(ResultSet resultSet = statement.executeQuery()){
                while(resultSet.next()){
                    DishOrder dishOrder = dishOrderMapper.apply(resultSet);
                    Long dishId = resultSet.getLong("id_dish");
                    Long dishOrderId = resultSet.getLong("id_dish_order");
                    dishOrder.setDish(findById(dishId));
                    dishOrder.setStatus(getDishOrderStatus(orderId, dishOrderId));
                    dishOrders.add(dishOrder);
                }
            }
        }
        return dishOrders;
    }

    @SneakyThrows
    public List<DishAndOrderStatus> getDishOrderStatus(Long orderId, Long dishOrderId){
       List<DishAndOrderStatus> dishAndOrderStatusList = new ArrayList<>();
       String sql = "select dos.id,dos.id_order_status,dos.id_dish_order, os.id_order, os.status, os.creation_date " +
               "from dish_order_status dos join order_status os on dos.id_order_status=os.id " +
               "where dos.id_dish_order=? and os.id_order=?";
       try(Connection connection = datasource.getConnection();
       PreparedStatement statement = connection.prepareStatement(sql)){
           statement.setLong(1, dishOrderId);
           statement.setLong(2, orderId);
           try(ResultSet resultSet = statement.executeQuery()){
               while(resultSet.next()){
                   DishAndOrderStatus dishAndOrderStatus = dishAndOrderStatusMapper.apply(resultSet);
                   dishAndOrderStatusList.add(dishAndOrderStatus);
               }
           }
       }
       return dishAndOrderStatusList;
    }

}
