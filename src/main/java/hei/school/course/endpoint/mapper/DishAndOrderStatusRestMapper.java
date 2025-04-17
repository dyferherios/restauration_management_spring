package hei.school.course.endpoint.mapper;

import hei.school.course.endpoint.rest.OrderRest;
import hei.school.course.model.DishAndOrderStatus;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DishAndOrderStatusRestMapper {

    @SneakyThrows
    public List<DishAndOrderStatus> toModel(OrderRest orderRest) {
        DishAndOrderStatus dishAndOrderStatus = new DishAndOrderStatus();
        dishAndOrderStatus.setStatus(orderRest.getStatus());
        dishAndOrderStatus.setDateValue(orderRest.getCreationDate().toInstant());
        return List.of(dishAndOrderStatus);
    }
}
