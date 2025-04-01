package hei.school.course.service;

import hei.school.course.entity.Criteria;
import hei.school.course.entity.Ingredient;
import hei.school.course.repository.CrudOperations;
import hei.school.course.repository.IngredientCrudOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class IngredientService {


    private final IngredientCrudOperations ingredientCrudOperations;

    public IngredientService(IngredientCrudOperations ingredientCrudOperations) {
        this.ingredientCrudOperations = ingredientCrudOperations;
    }

    public List<Ingredient> getAll(int page, int size) throws SQLException {
        return ingredientCrudOperations.getAll(page, size);
    }

    public Ingredient findById(Long id){
        System.out.println("passed");
        return ingredientCrudOperations.findById(id);
    }

    public List<Ingredient> filterByMaxAndMinPrice(Double priceMaxFilter, Double priceMinFilter, int page, int size) throws SQLException {
        if (priceMinFilter != null && priceMinFilter < 0) {
            throw new IllegalArgumentException("Error: " + priceMinFilter + " can't be negative.");
        }

        if (priceMaxFilter != null && priceMaxFilter < 0) {
            throw new IllegalArgumentException("Error: " + priceMaxFilter + " can't be negative.");
        }

        if (priceMinFilter != null && priceMaxFilter != null && priceMinFilter > priceMaxFilter) {
            throw new IllegalArgumentException("Error: " + priceMinFilter + " can't be greater than " + priceMaxFilter);
        }

        List<Ingredient> ingredients = ingredientCrudOperations.getAll(page, size);
        if (priceMaxFilter != null || priceMinFilter != null) {
            if (priceMinFilter == null) {
                ingredients = ingredients.stream()
                        .filter(ingredient -> ingredient.getActualPrice() <= priceMaxFilter)
                        .toList();
            } else if (priceMaxFilter == null) {
                ingredients = ingredients.stream()
                        .filter(ingredient -> ingredient.getActualPrice() >= priceMinFilter)
                        .toList();
            } else {
                ingredients = ingredients.stream()
                        .filter(ingredient -> ingredient.getActualPrice() >= priceMinFilter && ingredient.getActualPrice() <= priceMaxFilter)
                        .toList();
            }
        }

        return ingredients;
    }

    public List<Ingredient> saveAll(List<Ingredient> ingredients){
        return ingredientCrudOperations.saveAll(ingredients);
    }

}
