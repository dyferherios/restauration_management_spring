package hei.school.course.service;

import hei.school.course.dao.operations.DishCrudOperations;
import hei.school.course.dao.operations.OrderCrudOperations;
import hei.school.course.endpoint.mapper.DishOrderRestMapper;
import hei.school.course.endpoint.rest.DishOrderRest;
import hei.school.course.model.Criteria;
import hei.school.course.model.DishOrder;
import hei.school.course.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
   private final OrderCrudOperations orderCrudOperations;
   private final DishCrudOperations dishOrderCrudOperations;
   private final DishOrderRestMapper dishOrderRestMapper;

   public Order findByReference(String orderReference){
       return orderCrudOperations.findByCriteria(new Criteria("order_reference", orderReference));
   }

   public Order saveAll(Order order){
       System.out.println(order);
       List<Order> orderSaves = orderCrudOperations.saveAll(List.of(order));
       Order orderSaved;
       if(orderSaves!=null && !orderSaves.isEmpty()){
           orderSaved = orderSaves.getFirst();
       }else{
           orderSaved = orderCrudOperations.findByCriteria(new Criteria("order_reference", order.getReference()));
       }
       orderSaved.setStatus(order.getStatus());
       orderSaved.setStatus(orderCrudOperations.saveOrderStatus(orderSaved));

       if (order.getDishOrders()!=null && !order.getDishOrders().isEmpty()){
           List<DishOrder> dishOrders = order.getDishOrders();
           orderSaved.setDishOrders(dishOrders);
           dishOrderCrudOperations.saveAllDishOrder(orderSaved.getDishOrders());
       }
       return orderSaved;
   }
}
