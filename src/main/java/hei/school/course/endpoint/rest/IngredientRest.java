package hei.school.course.endpoint.rest;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class IngredientRest {
    private Long id;
    private String name;
    private List<PriceRest> prices;
    private List<StockMovementRest> stockMovements;
}
