package hei.school.course.controller;

import hei.school.course.entity.Ingredient;
import hei.school.course.service.IngredientService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;


@RestController
@AllArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping("/ingredients/filter")
    public ResponseEntity<Object> filterIngredients(
            @RequestParam(required = false) Double priceMinFilter,
            @RequestParam(required = false) Double priceMaxFilter,
            @RequestParam(required = false) int page,
            @RequestParam(required = false) int size) {
        try {
            List<Ingredient> filteredList = ingredientService.filterByMaxAndMinPrice(priceMaxFilter, priceMinFilter, page, size);
            return ResponseEntity.ok(filteredList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }

    @GetMapping("/ingredients/page")
    public ResponseEntity<List<Ingredient>> getAll(@RequestParam int page, @RequestParam int size){
        try{
            List<Ingredient> ingredients = ingredientService.getAll(page, size);
            System.out.println("passed");
            return ResponseEntity.ok(ingredients);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/ingredients/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id){
        try{
            Ingredient ingredient = ingredientService.findById(id);
            return ResponseEntity.ok(ingredient);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error : Ingredient with id : "+ id + " doesn't found");
        }
    }

    @PostMapping("/ingredients")
    public ResponseEntity<List<Ingredient>> saveAll(@RequestBody List<Ingredient> ingredients){
        try{
            List<Ingredient> ingredientsSaved = ingredientService.saveAll(ingredients);
            return ResponseEntity.ok(ingredientsSaved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
