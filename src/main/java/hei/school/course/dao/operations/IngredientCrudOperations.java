package hei.school.course.dao.operations;


import hei.school.course.dao.Datasource;
import hei.school.course.dao.mapper.DishIngredientMapper;
import hei.school.course.dao.mapper.IngredientMapper;
import hei.school.course.model.*;
import hei.school.course.service.exception.NotFoundException;
import hei.school.course.service.exception.ServerException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class IngredientCrudOperations implements CrudOperations<Ingredient> {
    private final Datasource dataSource;
    private final IngredientMapper ingredientMapper;
    private final PriceCrudOperations priceCrudOperations;
    private final StockMovementCrudOperations stockMovementCrudOperations;
    private final DishIngredientMapper dishIngredientMapper;

    @Override
    public List<Ingredient> getAll(int page, int size) {
        List<Ingredient> ingredients = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "select i.id, i.name from ingredient i order by i.id asc limit ? offset ?")) {
            statement.setInt(1, size);
            statement.setInt(2, size * (page - 1));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Ingredient ingredient = ingredientMapper.apply(resultSet);
                    ingredients.add(ingredient);
                }
                return ingredients;
            }
        } catch (SQLException e) {
            throw new ServerException(e);
        }
    }

    @Override
    public Ingredient findById(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "select i.id, i.name, di.id as dish_ingredient_id, di.required_quantity, di.unit from ingredient i"
                             + " left join dish_ingredient di on i.id = di.id_ingredient"
                             + " where i.id = ?")) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return ingredientMapper.apply(resultSet);
                }
                throw new NotFoundException("Ingredient.id=" + id + " not found");
            }
        } catch (SQLException e) {
            throw new ServerException(e);
        }
    }

    @SneakyThrows
    @Override
    public List<Ingredient> saveAll(List<Ingredient> entities) {
        List<Ingredient> ingredients = new ArrayList<>();
        String sqlWithId = "insert into ingredient (id, name) values (?, ?)"
                + " on conflict (id) do update set name=excluded.name"
                + " returning id, name";
        String sqlWithoutId = "insert into ingredient (name) values (?)"
                + " on conflict (name) do nothing"
                + " returning id, name";
        try (Connection connection = dataSource.getConnection()) {
                entities.forEach(entityToSave -> {
                    List<Price> prices = entityToSave.getPrices();
                    List<StockMovement> stockMovements = entityToSave.getStockMovements();
                    List<Price> addedPrices = List.of();
                    List<StockMovement> addedStockMovements = List.of();
                    if (entityToSave.getId() != null) {
                        try (PreparedStatement stmt = connection.prepareStatement(sqlWithId)) {
                            stmt.setLong(1, entityToSave.getId());
                            stmt.setString(2, entityToSave.getName());
                            try (ResultSet rs = stmt.executeQuery()) {
                                if (rs.next()) {
                                    Ingredient ingredient = ingredientMapper.apply(rs);
                                    if (prices!=null && !prices.isEmpty()){
                                        entityToSave.getPrices().forEach(price -> price.setIngredient(ingredient));
                                        addedPrices = priceCrudOperations.saveAll(entityToSave.getPrices());
                                    }
                                    if(stockMovements!=null && !stockMovements.isEmpty()){
                                        entityToSave.getStockMovements().forEach(stockMovement -> stockMovement.setIngredient(ingredient));
                                        addedStockMovements = stockMovementCrudOperations.saveAll(entityToSave.getStockMovements());
                                    }
                                    ingredient.addPrices(addedPrices);
                                    ingredient.addStockMovements(addedStockMovements);
                                    ingredients.add(ingredient);
                                }
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try (PreparedStatement stmt = connection.prepareStatement(sqlWithoutId)) {
                            stmt.setString(1, entityToSave.getName());
                            try (ResultSet rs = stmt.executeQuery()) {
                                if (rs.next()) {
                                    Ingredient ingredient = ingredientMapper.apply(rs);
                                     if (prices!=null && !prices.isEmpty()){
                                        entityToSave.getPrices().forEach(price -> price.setIngredient(ingredient));
                                        addedPrices = priceCrudOperations.saveAll(entityToSave.getPrices());
                                    }
                                    if(stockMovements!=null && !stockMovements.isEmpty()){
                                        entityToSave.getStockMovements().forEach(stockMovement -> stockMovement.setIngredient(ingredient));
                                        addedStockMovements = stockMovementCrudOperations.saveAll(entityToSave.getStockMovements());
                                    }
                                    ingredient.addPrices(addedPrices);
                                    ingredient.addStockMovements(addedStockMovements);
                                    ingredients.add(ingredient);
                                }
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        return ingredients;
    }

    @SneakyThrows
    public List<DishIngredient> findbyDishId(Long dishId){
        List<DishIngredient> dishIngredients = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "select i.id as id_ingredient, i.name, di.id as id, di.required_quantity, di.unit from ingredient i"
                        + " join dish_ingredient di on i.id = di.id_ingredient"
                        + " where di.id_dish = ?"
        )){
            statement.setLong(1, dishId);
            try(ResultSet resultSet=statement.executeQuery()){
                while(resultSet.next()){
                    DishIngredient dishIngredient;
                    dishIngredient = dishIngredientMapper.apply(resultSet);
                    dishIngredient.setIngredient(findById(resultSet.getLong("id")));
                    dishIngredients.add(dishIngredient);
                }
            }
        }
        return dishIngredients;
    }

    @SneakyThrows
    public List<DishIngredient> saveDishIngredient(Dish dish){
        List<DishIngredient> savedDishIngredients = new ArrayList<>();
        String sqlWithId = "insert into dish_ingredient (id, id_dish, id_ingredient, required_quantity, unit) " +
                "values(?, ?, ?, ?, ?::unit) on conflict (id, id_dish, id_ingredient) do update set required_quantity=excluded.required_quantity " +
                "returning id, id_dish, id_ingredient, required_quantity, unit";
        String sqlWithOutId = "insert into dish_ingredient (id_dish, id_ingredient, required_quantity, unit) " +
                "values(?, ?, ?, ?::unit) on conflict (id_dish, id_ingredient) do update set required_quantity=excluded.required_quantity " +
                "returning id, id_dish, id_ingredient, required_quantity, unit";
        try(Connection connection = dataSource.getConnection()){
            PreparedStatement statementWithId = connection.prepareStatement(sqlWithId);
            PreparedStatement statementWithOutId = connection.prepareStatement(sqlWithOutId);
            for (DishIngredient dishIngredient : dish.getDishIngredients()) {
                int index = 1;
                PreparedStatement statement;

                if (dishIngredient.getId() != null) {
                    statement = statementWithId;
                    statement.setLong(index++, dishIngredient.getId());
                    statement.setLong(index++, dish.getId());
                    statement.setLong(index++, dishIngredient.getIngredient().getId());
                    statement.setDouble(index++, dishIngredient.getRequiredQuantity());
                    statement.setString(index, String.valueOf(dishIngredient.getUnit()));
                } else {
                    statement = statementWithOutId;
                    statement.setLong(index++, dish.getId());
                    statement.setLong(index++, dishIngredient.getIngredient().getId());
                    statement.setDouble(index++, dishIngredient.getRequiredQuantity());
                    statement.setString(index, String.valueOf(dishIngredient.getUnit()));
                }
                try(ResultSet resultSet = statement.executeQuery()){
                   if(resultSet.next()){
                       savedDishIngredients = findbyDishId(resultSet.getLong("id_dish"));
                   }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        }
        return savedDishIngredients;
    }

}
