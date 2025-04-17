package hei.school.course.model;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Getter
@Setter
@Component

public class DishOrder {
    private Long id;
    private Dish dish;
    private Order order;
    private Double quantity;
    private List<DishAndOrderStatus> status;
}
