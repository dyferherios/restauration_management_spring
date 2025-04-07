package hei.school.course.endpoint;

import edu.hei.school.restaurant.service.exception.ClientException;
import edu.hei.school.restaurant.service.exception.NotFoundException;
import edu.hei.school.restaurant.service.exception.ServerException;

import hei.school.course.endpoint.mapper.DishIngredientRestMapper;
import hei.school.course.endpoint.mapper.DishRestMapper;
import hei.school.course.endpoint.rest.DishIngredientRest;
import hei.school.course.endpoint.rest.DishRest;
import hei.school.course.model.Dish;
import hei.school.course.model.DishIngredient;
import hei.school.course.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
public class DishRestController {
   private final DishService dishService;
   private final DishRestMapper dishRestMapper;
   private final DishIngredientRestMapper dishIngredientRestMapper;

   @GetMapping("/dishes")
   public ResponseEntity<Object> getAll(@RequestParam(required = false) Integer page,
                                        @RequestParam(required = false) Integer size){
       try {
           page = page != null ? page : 1;
           size = size != null ? size : 500;
           List<Dish> dishes = dishService.getAll(page, size);
           List<DishRest> dishRests = dishes.stream().map(dishRestMapper::toRest).toList();
           return ResponseEntity.ok().body(dishRests);
       }catch (ClientException e) {
           return ResponseEntity.badRequest().body(e.getMessage());
       } catch (NotFoundException e) {
           return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
       } catch (ServerException e) {
           return ResponseEntity.internalServerError().body(e.getMessage());
       }
   }

   @PutMapping("/dishes/{id}/ingredients")
    public ResponseEntity<Object> addIngredients(@PathVariable Long id,
                                                 @RequestBody List<DishIngredientRest> dishIngredientRests) {

       try{
           List<DishIngredient> dishIngredients = dishIngredientRests.stream()
               .map(dishIngredientRestMapper::toModel).toList();
           Dish dish = dishService.addDishIngredient(id, dishIngredients);
           DishRest dishRest = dishRestMapper.toRest(dish);
           return  ResponseEntity.ok().body(dishRest);
       }catch (ClientException e) {
           return ResponseEntity.badRequest().body(e.getMessage());
       } catch (NotFoundException e) {
           return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
       } catch (ServerException e) {
           return ResponseEntity.internalServerError().body(e.getMessage());
       }
   }
}
