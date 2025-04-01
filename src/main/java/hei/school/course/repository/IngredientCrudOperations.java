package hei.school.course.repository;

import hei.school.course.entity.Criteria;
import hei.school.course.configuration.Datasource;
import hei.school.course.entity.*;
import hei.school.course.repository.mapper.IngredientMapper;
import hei.school.course.repository.mapper.PriceMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class IngredientCrudOperations implements CrudOperations<Ingredient> {
    private final Datasource dataSource;
    private final PriceCrudOperations priceCrudOperations;
    private final StockMovementCrudOperations stockMovementCrudOperations;
    private final IngredientMapper ingredientMapper;

    @Override
    public List<Ingredient> getAll(int page, int size) {
        List<Ingredient> ingredients = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select i.id, i.name, di.id as dish_ingredient_id, di.required_quantity, di.unit from " +
                    "ingredient i join dish_ingredient di on i.id = di.id_ingredient limit ? offset ?")) {
            preparedStatement.setInt(1, size);
            preparedStatement.setInt(2, size*(page-1));
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                ingredients.add(ingredientMapper.map(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return  ingredients;
    }

    @Override
    public Ingredient findById(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select i.id, i.name, di.id as dish_ingredient_id, di.required_quantity, di.unit from ingredient i"
                     + " join dish_ingredient di on i.id = di.id_ingredient"
                     + " where i.id = ?")) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return ingredientMapper.map(resultSet);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Ingredient> filterByCriteria(List<Criteria> criterias, int page, int size, Map<String, String> sort) {
        List<Ingredient> ingredients = new ArrayList<>();
//        StringBuilder query = new StringBuilder("select i.id, i.name, p.id as id_price, sm.id as sm_id from ingredient i " +
//                "join price p on i.id = p.id_ingredient " +
//                "join stock_movement sm on sm.id_ingredient = i.id where 1=1 ");
//        if(!criterias.isEmpty()){
//            criterias.forEach(criteria -> {
//                if(criteria.getKey() == "name"){
//                    query.append(criteria.getConjunction()).append(" ").append(criteria.getKey()).append(" ").append(criteria.getOperation()).append(" '%").append(criteria.getValue()).append("%' ");
//            });
//        }
//
//
//        try (Connection connection = dataSource.getConnection();
//             PreparedStatement statement = connection.prepareStatement(query)) {
//
//            try (ResultSet resultSet = statement.executeQuery()) {
//                while (resultSet.next()) {
//                    ingredients.add(mapFromResultSet(resultSet));
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
        return null;
    }

    @SneakyThrows
    @Override
    public List<Ingredient> saveAll(List<Ingredient> entities) {
        List<Ingredient> ingredients = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement =
                         connection.prepareStatement("insert into ingredient (id, name) values (?, ?)"
                                 + " on conflict (id) do update set name=excluded.name"
                                 + " returning id, name")) {
                entities.forEach(entityToSave -> {
                    try {

                        statement.setLong(1, entityToSave.getId());
                        statement.setString(2, entityToSave.getName());
                        statement.addBatch();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                try (ResultSet resultSet = statement.executeQuery()) {
                    entities.forEach(entityToSave -> {
                        try {
                            priceCrudOperations.saveAll(entityToSave.getPrices());
                            stockMovementCrudOperations.saveAll(entityToSave.getStockMovements());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                    while (resultSet.next()) {
                        ingredients.add(ingredientMapper.map(resultSet));
                    }
                }
                return ingredients;
            }
        }
    }

//    public List<DishIngredient> findByDishId(Long dishId) {
//        List<DishIngredient> dishIngredients = new ArrayList<>();
//        try (Connection connection = dataSource.getConnection();
//             PreparedStatement statement = connection.prepareStatement("select i.id, i.name, di.id as dish_ingredient_id, di.required_quantity, di.unit from ingredient i"
//                     + " join dish_ingredient di on i.id = di.id_ingredient"
//                     + " where di.id_dish = ?")) {
//            statement.setLong(1, dishId);
//            try (ResultSet resultSet = statement.executeQuery()) {
//                while (resultSet.next()) {
//                    Ingredient ingredient = mapFromResultSet(resultSet);
//                    DishIngredient dishIngredient = mapDishIngredient(resultSet, ingredient);
//                    dishIngredients.add(dishIngredient);
//                }
//                return dishIngredients;
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }


//    private DishIngredient mapDishIngredient(ResultSet resultSet, Ingredient ingredient) throws SQLException {
//        double requiredQuantity = resultSet.getDouble("required_quantity");
//        Unit unit = Unit.valueOf(resultSet.getString("unit"));
//        Long dishIngredientId = resultSet.getLong("dish_ingredient_id");
//        return new DishIngredient(dishIngredientId, ingredient, requiredQuantity, unit);
//    }
}
