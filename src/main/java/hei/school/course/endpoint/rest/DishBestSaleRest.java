package hei.school.course.endpoint.rest;

import lombok.*;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Data
@Component
public class DishBestSaleRest {
    private Long id;
    private String dishName;
    private Double quantity;
    private Double amountTotal;
}
