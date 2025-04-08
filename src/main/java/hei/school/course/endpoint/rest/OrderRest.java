package hei.school.course.endpoint.rest;

import hei.school.course.model.DishOrder;
import hei.school.course.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Component
public class OrderRest {
   private Long id;
   private String reference;
   private List<DishOrderRest> dishOrderRests;
   private Status status;
}
