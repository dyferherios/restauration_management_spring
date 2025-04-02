package hei.school.course.dao.mapper;

import hei.school.course.dao.operations.PriceCrudOperations;
import hei.school.course.dao.operations.StockMovementCrudOperations;
import hei.school.course.model.Ingredient;
import hei.school.course.model.Price;
import hei.school.course.model.StockMovement;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class IngredientMapper implements Function<ResultSet, Ingredient> {
    private final PriceCrudOperations priceCrudOperations;
    private final StockMovementCrudOperations stockMovementCrudOperations;

    @SneakyThrows
    @Override
    public Ingredient apply(ResultSet resultSet) {
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