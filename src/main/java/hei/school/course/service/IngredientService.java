package hei.school.course.service;


import hei.school.course.dao.operations.IngredientCrudOperations;
import hei.school.course.dao.operations.PriceCrudOperations;
import hei.school.course.dao.operations.StockMovementCrudOperations;
import hei.school.course.model.Ingredient;
import hei.school.course.model.Price;
import hei.school.course.model.StockMovement;
import hei.school.course.service.exception.ClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientCrudOperations ingredientCrudOperations;
    private final PriceCrudOperations priceCrudOperations;
    private final StockMovementCrudOperations stockMovementCrudOperations;

    public List<Ingredient> getIngredientsByPrices(Double priceMinFilter, Double priceMaxFilter, Integer page, Integer size) {
        if (priceMinFilter != null && priceMinFilter < 0) {
            throw new ClientException("PriceMinFilter " + priceMinFilter + " is negative");
        }
        if (priceMaxFilter != null && priceMaxFilter < 0) {
            throw new ClientException("PriceMaxFilter " + priceMaxFilter + " is negative");
        }
        if (priceMinFilter != null && priceMaxFilter != null) {
            if (priceMinFilter > priceMaxFilter) {
                throw new ClientException("PriceMinFilter " + priceMinFilter + " is greater than PriceMaxFilter " + priceMaxFilter);
            }
        }

        List<Ingredient> ingredients = ingredientCrudOperations.getAll(page, size);

        return ingredients.stream()
                .filter(ingredient -> {
                    if (priceMinFilter == null && priceMaxFilter == null) {
                        return true;
                    }
                    Double unitPrice = ingredient.getActualPrice();
                    if (priceMinFilter != null && priceMaxFilter == null) {
                        return unitPrice >= priceMinFilter;
                    }
                    if (priceMinFilter == null) {
                        return unitPrice <= priceMaxFilter;
                    }
                    return unitPrice >= priceMinFilter && unitPrice <= priceMaxFilter;
                })
                .toList();
    }

    public List<Ingredient> getAll(Integer page, Integer size) {
        return ingredientCrudOperations.getAll(page, size);
    }

    public Ingredient getById(Long id) {
        return ingredientCrudOperations.findById(id);
    }

    public List<Ingredient> saveAll(List<Ingredient> ingredients) {
        return ingredientCrudOperations.saveAll(ingredients);
    }

    public Ingredient addPrices(Long ingredientId, List<Price> pricesToAdd) {
        Ingredient ingredient = ingredientCrudOperations.findById(ingredientId);
        pricesToAdd.forEach(price -> price.setIngredient(ingredient));
        priceCrudOperations.saveAll(pricesToAdd);
        return ingredientCrudOperations.findById(ingredientId);
    }

    public Ingredient addStockMovements(Long ingredientId, List<StockMovement> stockMovementToAdd){
        Ingredient ingredient = ingredientCrudOperations.findById(ingredientId);
        stockMovementToAdd.forEach(stockMovement -> stockMovement.setIngredient(ingredient));
        stockMovementCrudOperations.saveAll(stockMovementToAdd);
        return ingredientCrudOperations.findById(ingredientId);
    }
}
