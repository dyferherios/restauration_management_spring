package hei.school.course.endpoint.rest;

import hei.school.course.model.DishIngredient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DishRest {
    private Long id;
    private String name;
    private List<DishIngredientRest> dishIngredients;
    private Double price;
    private Double availableQuantity;
}
