package hei.school.course.endpoint.rest;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IngredientBasicRest {
    private Long id;
    private String name;
    private Double actualPrice;
    private Double actualStock;
}
