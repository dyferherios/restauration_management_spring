package hei.school.course.endpoint.rest;

import hei.school.course.model.Status;
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
public class DishOrderRest {
    private Long id;
    private String name;
    private Double price;
    private Double quantity;
    private Status status;
}
