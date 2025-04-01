package hei.school.course.repository.mapper;

import hei.school.course.entity.Ingredient;
import hei.school.course.entity.Price;
import hei.school.course.entity.StockMovement;
import hei.school.course.repository.PriceCrudOperations;
import hei.school.course.repository.StockMovementCrudOperations;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@AllArgsConstructor
public class IngredientMapper {
    private final PriceCrudOperations priceCrudOperations;
    private final StockMovementCrudOperations stockMovementCrudOperations;

    public Ingredient map(ResultSet resultSet) throws SQLException {
        Long idIngredient = resultSet.getLong("id");
        List<Price> ingredientPrices = priceCrudOperations.findByIdIngredient(idIngredient);
        List<StockMovement> ingredientStockMovements = stockMovementCrudOperations.findByIdIngredient(idIngredient);
        Ingredient ingredient = new Ingredient();
        ingredient.setId(idIngredient);
        ingredient.setName(resultSet.getString("name"));
        ingredient.setPrices(ingredientPrices);
        ingredient.setStockMovements(ingredientStockMovements);
        return ingredient;
    }
}
