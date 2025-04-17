package hei.school.course.endpoint.mapper;

import hei.school.course.dao.operations.DishCrudOperations;
import hei.school.course.endpoint.rest.DishOrderRest;
import hei.school.course.model.Criteria;
import hei.school.course.model.DishAndOrderStatus;
import hei.school.course.model.DishOrder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class DishOrderRestMapper implements Function<DishOrder, DishOrderRest> {

    @Autowired private final DishCrudOperations dishCrudOperations;

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
        DishOrder dishOrder = new DishOrder();
        if(dishOrderRest.getId() != null){
            dishOrder.setId(dishOrderRest.getId());
        }
        dishOrder.setQuantity(dishOrderRest.getQuantity());
        dishOrder.setDish(dishCrudOperations.findByCriteria(new Criteria("name",dishOrderRest.getName())));
        return dishOrder;
    }
}
