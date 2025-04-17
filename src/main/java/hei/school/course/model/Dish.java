package hei.school.course.model;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Getter
@Setter
@ToString(exclude = {"dishIngredients"})
public class Dish {
    private Long id;
    private String name;
    private List<DishIngredient> dishIngredients;
    private Double price;

    public Double getGrossMargin() {
        return getPrice() - getTotalIngredientsCost();
    }

    public Double getGrossMarginAt(LocalDate dateValue) {
        return getPrice() - getTotalIngredientsCostAt(dateValue);
    }

    public Double getTotalIngredientsCost() {
        return dishIngredients.stream()
                .map(dishIngredient -> {
                    Double actualPrice = dishIngredient.getIngredient().getActualPrice();
                    Double requiredQuantity = dishIngredient.getRequiredQuantity();
                    return actualPrice * requiredQuantity;
                })
                .reduce(0.0, Double::sum);
    }

    public Double getTotalIngredientsCostAt(LocalDate dateValue) {
        double cost = 0.0;
        for (DishIngredient dishIngredient : dishIngredients) {
            cost += dishIngredient.getIngredient().getPriceAt(dateValue);
        }
        return cost;
    }

    public Double getAvailableQuantity() {
        List<Double> allQuantitiesPossible = new ArrayList<>();
        for (DishIngredient dishIngredient : dishIngredients) {
            Ingredient ingredient = dishIngredient.getIngredient();
            double quantityPossibleForThatIngredient = ingredient.getAvailableQuantity() / dishIngredient.getRequiredQuantity();
            double roundedQuantityPossible = Math.ceil(quantityPossibleForThatIngredient); // ceil for smallest
            allQuantitiesPossible.add(roundedQuantityPossible);
        }
        return allQuantitiesPossible.stream().min(Double::compare).orElse(0.0);
    }

    public Double getAvailableQuantityAt(Instant datetime) {
        List<Double> allQuantitiesPossible = new ArrayList<>();
        for (DishIngredient dishIngredient : dishIngredients) {
            Ingredient ingredient = dishIngredient.getIngredient();
            double quantityPossibleForThatIngredient = ingredient.getAvailableQuantityAt(datetime) / dishIngredient.getRequiredQuantity();
            double roundedQuantityPossible = Math.ceil(quantityPossibleForThatIngredient); // ceil for smallest
            allQuantitiesPossible.add(roundedQuantityPossible);
        }
        return allQuantitiesPossible.stream().min(Double::compare).orElse(0.0);
    }
}
