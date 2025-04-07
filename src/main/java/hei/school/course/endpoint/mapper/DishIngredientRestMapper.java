package hei.school.course.endpoint.mapper;

import hei.school.course.endpoint.rest.DishIngredientRest;
import hei.school.course.model.Dish;
import hei.school.course.model.DishIngredient;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class DishIngredientRestMapper implements Function<DishIngredient, DishIngredientRest> {
    @Autowired private DishIngredientBasicRestMapper dishIngredientBasicRestMapper;

    @Override
    public DishIngredientRest apply(DishIngredient dishIngredient) {
        return new DishIngredientRest(
                dishIngredient.getId(),
                dishIngredientBasicRestMapper.apply(dishIngredient.getIngredient()),
                dishIngredient.getRequiredQuantity(),
                dishIngredient.getUnit()
        );
    }

    public DishIngredient toModel(DishIngredientRest dishIngredientRest){
       DishIngredient dishIngredient = new DishIngredient();
       if(dishIngredientRest.getId()!=null){
           dishIngredient.setId(dishIngredientRest.getId());
       }
       dishIngredient.setRequiredQuantity(dishIngredientRest.getRequiredQuantity());
       dishIngredient.setUnit(dishIngredientRest.getUnit());
        if (dishIngredientRest.getIngredientBasicRest() != null) {
            dishIngredient.setIngredient(
                    dishIngredientBasicRestMapper.toModel(dishIngredientRest.getIngredientBasicRest())
            );
        } else {
            throw new IllegalArgumentException("ingredientBasicRest cannot be null");
        }
       return dishIngredient;
    }
}
