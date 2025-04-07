package hei.school.course.endpoint.rest;

import hei.school.course.model.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Component
public class DishIngredientRest {
    private Long id;
    private IngredientBasicRest ingredientBasicRest;
    private Double requiredQuantity;
    private Unit unit;
}
