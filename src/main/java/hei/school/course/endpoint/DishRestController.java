package hei.school.course.endpoint;

import hei.school.course.endpoint.mapper.DishRestMapper;
import hei.school.course.endpoint.rest.DishRest;
import hei.school.course.model.Dish;
import hei.school.course.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
public class DishRestController {
   private final DishService dishService;
   private final DishRestMapper dishRestMapper;

   @GetMapping("/dishes")
   public ResponseEntity<Object> getAll(@RequestParam(required = false) Integer page,
                                        @RequestParam(required = false) Integer size){
       try {
           page = page != null ? page : 1;
           size = size != null ? size : 500;
           List<Dish> dishes = dishService.getAll(page, size);
           List<DishRest> dishRests = dishes.stream().map(dishRestMapper::toRest).toList();
           return ResponseEntity.ok().body(dishRests);
       }catch (edu.hei.school.restaurant.service.exception.ClientException e) {
           return ResponseEntity.badRequest().body(e.getMessage());
       } catch (edu.hei.school.restaurant.service.exception.NotFoundException e) {
           return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
       } catch (edu.hei.school.restaurant.service.exception.ServerException e) {
           return ResponseEntity.internalServerError().body(e.getMessage());
       }
   }
}
