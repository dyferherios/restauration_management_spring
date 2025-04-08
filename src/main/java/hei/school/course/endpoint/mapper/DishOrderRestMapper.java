package hei.school.course.endpoint.mapper;

import hei.school.course.endpoint.rest.DishOrderRest;
import hei.school.course.model.DishAndOrderStatus;
import hei.school.course.model.DishOrder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class DishOrderRestMapper implements Function<DishOrder, DishOrderRest> {

    @Override
    public DishOrderRest apply(DishOrder dishOrder) {
       DishOrderRest dishOrderRest = new DishOrderRest();
       dishOrderRest.setId(dishOrder.getId());
       dishOrderRest.setName(dishOrder.getDish().getName());
       dishOrderRest.setQuantity(dishOrder.getQuantity());
       List<DishAndOrderStatus> statusList = dishOrder.getStatus();
       dishOrderRest.setStatus(statusList.getLast().getStatus());
       dishOrderRest.setPrice(dishOrder.getDish().getPrice());
       return dishOrderRest;
    }

    public DishOrder toModel(DishOrderRest dishOrderRest){
        return null;
    }
}
