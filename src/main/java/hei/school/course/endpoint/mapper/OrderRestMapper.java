package hei.school.course.endpoint.mapper;

import hei.school.course.endpoint.rest.DishOrderRest;
import hei.school.course.endpoint.rest.OrderRest;
import hei.school.course.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class OrderRestMapper {
    @Autowired private final DishOrderRestMapper dishOrderRestMapper;

    public OrderRest toRest(Order order){
       OrderRest orderRest = new OrderRest();
       orderRest.setId(order.getId());
       orderRest.setReference(orderRest.getReference());
       if(order.getDishOrders() != null && !order.getDishOrders().isEmpty() ){
          List<DishOrderRest> dishOrderRests = order.getDishOrders().stream()
                          .map(dishOrderRestMapper::apply)
                                  .toList();
          orderRest.setReference(order.getReference());
          orderRest.setDishOrderRests(dishOrderRests);
          orderRest.setStatus(order.getActualStatus());
       }

       return orderRest;
    }
}
