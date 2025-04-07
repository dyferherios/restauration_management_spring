package hei.school.course.endpoint.mapper;

import hei.school.course.endpoint.rest.DishIngredientRest;
import hei.school.course.model.DishIngredient;
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
                dishIngredient.getRequiredQuantity()
        );
    }
}
