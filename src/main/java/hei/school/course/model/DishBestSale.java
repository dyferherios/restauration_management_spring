package hei.school.course.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Setter
@Getter
public class DishBestSale {
    private Dish dish;
    private Double quantity;
    private Double amountTotal;
}
