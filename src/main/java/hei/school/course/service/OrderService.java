package hei.school.course.service;

import hei.school.course.dao.operations.DishCrudOperations;
import hei.school.course.dao.operations.OrderCrudOperations;
import hei.school.course.endpoint.mapper.DishOrderRestMapper;
import hei.school.course.endpoint.rest.DishOrderRest;
import hei.school.course.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
   private final OrderCrudOperations orderCrudOperations;
   private final DishCrudOperations dishCrudOperations;
   private final DishOrderRestMapper dishOrderRestMapper;

   public Order findByReference(String orderReference){
       return orderCrudOperations.findByCriteria(new Criteria("order_reference", orderReference));
   }

   public Order saveAll(Order order){
       List<Order> orderSaves = orderCrudOperations.saveAll(List.of(order));
       Order orderSaved;
       System.out.println("order " + order);
       if(orderSaves!=null && !orderSaves.isEmpty()){
           orderSaved = orderSaves.getFirst();
       }else{
           System.out.println("passed here to get by criteria");
           orderSaved = orderCrudOperations.findByCriteria(new Criteria("order_reference", order.getReference()));
           System.out.println("order saved : " + orderSaved);
       }
       orderSaved.setStatus(order.getStatus());
       System.out.println("order to save status : " + orderSaved);
       List<DishAndOrderStatus> statusList = orderCrudOperations.saveOrderStatus(orderSaved);
       if(statusList.isEmpty()){
           statusList = orderCrudOperations.getOrderStatus(orderSaved.getId());
       }
       orderSaved.setStatus(statusList);
       System.out.println("order saved with status list : " + orderSaved);
       if(orderSaved.getDishOrders()!=null){
           List<DishOrder> dishOrders = getDishOrders(orderSaved, statusList.getLast());
           saveDishAndOrderStatus(dishOrders);
           System.out.println("dish orders : " + dishOrders);
       }

       System.out.println(orderSaved);
       return orderSaved;
   }

   public Order addDishOrder(Order order){
       DishAndOrderStatus status = orderCrudOperations.getOrderStatus(order.getId()).getLast();
       status.setOrder(order);
       List<DishOrder> dishOrders = getDishOrders(order, status);
       order.setDishOrders(saveAllDishOrder(dishOrders));
       List<DishOrder> dishOrderList = saveAllDishOrder(dishOrders);
       if(dishOrderList==null){
           dishOrderList = dishCrudOperations.findByOrderId(order.getId());
       }
       order.setDishOrders(dishOrderList);
       List<DishOrder> dishOrdersSaved = getDishOrders(order, status);
       List<DishAndOrderStatus> statusList = saveDishAndOrderStatus(dishOrdersSaved);
       if(statusList==null){
              dishOrdersSaved.forEach(dishOrder -> dishOrder.setStatus(dishCrudOperations.getDishOrderStatus(order.getId(), dishOrder.getId())));
       }
       return order;
   }

    private static List<DishOrder> getDishOrders(Order order, DishAndOrderStatus statusList) {
        List<DishOrder> dishOrders = order.getDishOrders();
        statusList.setOrder(order);
        dishOrders.forEach(dishOrder -> dishOrder.setOrder(order));
        dishOrders.forEach(dishOrder -> dishOrder.setStatus(List.of(statusList)));

        if(statusList.getStatus().equals(Status.CONFIRMED)) {
            dishOrders.forEach(dishOrder -> {
                    if(dishOrder.getDish().getAvailableQuantity() < dishOrder.getQuantity()) {
                           throw new RuntimeException("Dish " + dishOrder.getDish().getName() + " is not available in the requested quantity");
                    }
            });
        }

        return dishOrders;
    }

    public List<DishOrder> saveAllDishOrder(List<DishOrder> dishOrders){
       return dishCrudOperations.saveAllDishOrder(dishOrders);
   }

   public List<DishAndOrderStatus> saveDishAndOrderStatus(List<DishOrder> dishOrderList){
       return dishCrudOperations.saveDishOrderStatus(dishOrderList);
   }
}
