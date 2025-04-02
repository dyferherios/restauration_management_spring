package hei.school.course.endpoint.mapper;


import edu.hei.school.restaurant.service.exception.NotFoundException;
import hei.school.course.dao.operations.IngredientCrudOperations;
import hei.school.course.endpoint.rest.CreateOrUpdateIngredient;
import hei.school.course.endpoint.rest.IngredientRest;
import hei.school.course.endpoint.rest.PriceRest;
import hei.school.course.endpoint.rest.StockMovementRest;
import hei.school.course.model.Ingredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IngredientRestMapper {
    @Autowired private PriceRestMapper priceRestMapper;
    @Autowired private StockMovementRestMapper stockMovementRestMapper;
    @Autowired private IngredientCrudOperations ingredientCrudOperations;

    public IngredientRest toRest(Ingredient ingredient) {
        List<PriceRest> prices = ingredient.getPrices().stream()
                .map(price -> priceRestMapper.apply(price)).toList();
        List<StockMovementRest> stockMovementRests = ingredient.getStockMovements().stream()
                .map(stockMovement -> stockMovementRestMapper.apply(stockMovement))
                .toList();
        return new IngredientRest(ingredient.getId(), ingredient.getName(), prices, stockMovementRests);
    }

    public Ingredient toModel(CreateOrUpdateIngredient newIngredient) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(newIngredient.getName());
        if(newIngredient.getId()!=null){
            ingredient.setId(newIngredient.getId());
            try {
                Ingredient existingIngredient = ingredientCrudOperations.findById(newIngredient.getId());
                ingredient.addPrices(existingIngredient.getPrices());
                ingredient.addStockMovements(existingIngredient.getStockMovements());
            } catch (NotFoundException e) {
                ingredient.addPrices(new ArrayList<>());
                ingredient.addStockMovements(new ArrayList<>());
            }
        }else{
            ingredient.setPrices(new ArrayList<>());
            ingredient.setStockMovements(new ArrayList<>());
        }

        return ingredient;
    }
}
