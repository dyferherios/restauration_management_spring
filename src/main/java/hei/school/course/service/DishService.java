package hei.school.course.service;

import hei.school.course.dao.operations.DishCrudOperations;
import hei.school.course.endpoint.mapper.DishIngredientRestMapper;
import hei.school.course.endpoint.rest.DishIngredientRest;
import hei.school.course.model.Dish;
import hei.school.course.model.DishIngredient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DishService {
    private final DishCrudOperations dishCrudOperations;
    private final DishIngredientRestMapper dishIngredientRestMapper;

    public List<Dish> getAll(int page, int size){
        return dishCrudOperations.getAll(page, size);
    }

    public Dish addDishIngredient(Long dishId, List<DishIngredient> dishIngredients){
       Dish dish = dishCrudOperations.findById(dishId);
       dish.setDishIngredients(dishIngredients);
       List<Dish> dishes = dishCrudOperations.saveAll(List.of(dish));
       return dishes.getFirst();
    }
}
