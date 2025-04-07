package hei.school.course.endpoint.mapper;

import hei.school.course.endpoint.rest.IngredientBasicRest;
import hei.school.course.model.Ingredient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.function.Function;

@Component
public class DishIngredientBasicRestMapper implements Function<Ingredient, IngredientBasicRest> {
    @Override
    public IngredientBasicRest apply(Ingredient ingredient) {
        return new IngredientBasicRest(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getActualPrice(),
                ingredient.getAvailableQuantity());
    }

    public Ingredient toModel(IngredientBasicRest ingredientBasicRest){
        Ingredient ingredient = new Ingredient();
        if(ingredientBasicRest.getId()!=null){
            ingredient.setId(ingredientBasicRest.getId());
        }
        ingredient.setName(ingredientBasicRest.getName());
        ingredient.setPrices(new ArrayList<>());
        ingredient.setStockMovements(new ArrayList<>());
        return ingredient;
    }
}
