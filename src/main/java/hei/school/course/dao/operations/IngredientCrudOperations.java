package hei.school.course.dao.operations;


import edu.hei.school.restaurant.service.exception.NotFoundException;
import edu.hei.school.restaurant.service.exception.ServerException;
import hei.school.course.dao.Datasource;
import hei.school.course.dao.mapper.IngredientMapper;
import hei.school.course.model.Ingredient;
import hei.school.course.model.Price;
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
                    if (entityToSave.getId() != null) {
                        try (PreparedStatement stmt = connection.prepareStatement(sqlWithId)) {
                            stmt.setLong(1, entityToSave.getId());
                            stmt.setString(2, entityToSave.getName());
                            try (ResultSet rs = stmt.executeQuery()) {
                                if (rs.next()) {
                                    Ingredient ingredient = ingredientMapper.apply(rs);
                                    entityToSave.getPrices().forEach(price -> price.setIngredient(ingredient));
                                    priceCrudOperations.saveAll(entityToSave.getPrices());
                                    ingredient.setStockMovements(entityToSave.getStockMovements());
                                    stockMovementCrudOperations.saveAll(ingredient.getStockMovements());
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
//                                    ingredient.setPrices(entityToSave.getPrices());
//                                    priceCrudOperations.saveAll(ingredient.getPrices());
//                                    ingredient.setStockMovements(entityToSave.getStockMovements());
//                                    stockMovementCrudOperations.saveAll(ingredient.getStockMovements());
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
}
