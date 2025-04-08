package hei.school.course.endpoint.mapper;


import hei.school.course.dao.operations.IngredientCrudOperations;
import hei.school.course.endpoint.rest.CreateOrUpdateIngredient;
import hei.school.course.endpoint.rest.IngredientRest;
import hei.school.course.endpoint.rest.PriceRest;
import hei.school.course.endpoint.rest.StockMovementRest;
import hei.school.course.model.Ingredient;
import hei.school.course.model.Price;
import hei.school.course.model.StockMovement;
import hei.school.course.service.exception.NotFoundException;
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
        ingredient.setPrices(new ArrayList<>());
        ingredient.setStockMovements(new ArrayList<>());
        if(newIngredient.getPrices()!=null && !newIngredient.getPrices().isEmpty()){
            List<Price> prices = new ArrayList<>(newIngredient.getPrices().stream()
                    .map(priceRest -> priceRestMapper.toModel(priceRest))
                    .toList());
            ingredient.setPrices(prices);
        }

        if(newIngredient.getStockMovements()!=null && !newIngredient.getStockMovements().isEmpty()){
            List<StockMovement> stockMovements = new ArrayList<>(newIngredient.getStockMovements().stream()
                    .map(stockMovementRest -> stockMovementRestMapper.toModel(stockMovementRest)).toList());
            ingredient.setStockMovements(stockMovements);
        }

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
        }

        return ingredient;
    }
}
