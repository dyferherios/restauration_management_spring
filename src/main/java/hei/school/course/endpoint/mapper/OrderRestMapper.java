package hei.school.course.endpoint.mapper;

import hei.school.course.endpoint.rest.DishOrderRest;
import hei.school.course.endpoint.rest.OrderRest;
import hei.school.course.model.DishOrder;
import hei.school.course.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderRestMapper {
    @Autowired private final DishOrderRestMapper dishOrderRestMapper;
    @Autowired private final DishAndOrderStatusRestMapper dishAndOrderStatusMapper;

    public OrderRest toRest(Order order){
       OrderRest orderRest = new OrderRest();
       orderRest.setId(order.getId());
       orderRest.setReference(order.getReference());
       if(order.getDishOrders() != null && !order.getDishOrders().isEmpty() ){
          List<DishOrderRest> dishOrderRests = order.getDishOrders().stream()
                          .map(dishOrderRestMapper::apply)
                                  .toList();
          orderRest.setReference(order.getReference());
          orderRest.setDishOrderRests(dishOrderRests);
          orderRest.setStatus(order.getActualStatus());
       }
       orderRest.setStatus(order.getActualStatus());
       orderRest.setCreationDate(order.getCreationDate());
       return orderRest;
    }

    public Order toModel(OrderRest orderRest){
        Order order = new Order();
        order.setId(orderRest.getId());
        order.setReference(orderRest.getReference());
        order.setCreationDate(orderRest.getCreationDate());
        order.setStatus(dishAndOrderStatusMapper.toModel(orderRest));
        if(orderRest.getDishOrderRests() != null && !orderRest.getDishOrderRests().isEmpty()){
            List<DishOrder> dishOrderRests = orderRest.getDishOrderRests().stream()
                    .map(dishOrderRestMapper::toModel)
                    .toList();
            dishOrderRests.forEach(dishOrder -> dishOrder.setStatus(order.getStatus()));
            order.setDishOrders(dishOrderRests);
        }
        return order;
    }
}
