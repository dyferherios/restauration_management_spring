package hei.school.course.endpoint.mapper;

import hei.school.course.endpoint.rest.DishIngredientRest;
import hei.school.course.endpoint.rest.DishRest;
import hei.school.course.model.Dish;
import hei.school.course.model.DishIngredient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DishRestMapper {
    @Autowired DishIngredientRestMapper dishIngredientRestMapper;

    public DishRest toRest(Dish dish){

        List<DishIngredientRest> dishIngredientRestList = dish.getDishIngredients().stream()
                .map(dishIngredient -> dishIngredientRestMapper.apply(dishIngredient))
                .toList();
        return new DishRest(
                dish.getId(),
                dish.getName(),
                dishIngredientRestList,
                dish.getPrice(),
                dish.getAvailableQuantity()
        );
    }

    public Dish toModel(DishRest dishRest){
        Dish dish = new Dish();
        if(dishRest.getId()!=null){
            dish.setId(dishRest.getId());
        }
        dish.setName(dishRest.getName());
        dish.setPrice(dishRest.getPrice());
        List<DishIngredient> dishIngredients = dishRest.getDishIngredients().stream()
                        .map(dishIngredientRest -> dishIngredientRestMapper.toModel(dishIngredientRest)).toList();
        dish.setDishIngredients(dishIngredients);
        return dish;
    }
}
