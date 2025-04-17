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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
       if(orderSaves!=null && !orderSaves.isEmpty()){
           orderSaved = orderSaves.getFirst();
       }else{
           orderSaved = orderCrudOperations.findByCriteria(new Criteria("order_reference", order.getReference()));
       }
       orderSaved.setStatus(order.getStatus());
       List<DishAndOrderStatus> statusList = orderCrudOperations.saveOrderStatus(orderSaved);
       if(statusList.isEmpty()){
           statusList = orderCrudOperations.getOrderStatus(orderSaved.getId());
       }
       orderSaved.setStatus(statusList);
       if(orderSaved.getDishOrders()!=null){
           List<DishOrder> dishOrders = getDishOrders(orderSaved, statusList.getLast());
           saveDishAndOrderStatus(dishOrders);
       }

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

   public DishOrder updateDishOrderStatus(DishOrder dishOrder){
       List<DishAndOrderStatus> dishAndOrderStatusList = dishCrudOperations.saveDishOrderStatus(List.of(dishOrder));
       if(dishAndOrderStatusList.isEmpty()){
           dishAndOrderStatusList = dishCrudOperations.getDishOrderStatus(dishOrder.getOrder().getId(), dishOrder.getId());
       }
       dishOrder.setStatus(dishAndOrderStatusList);
       Status currentStatus = dishOrder.getOrder().getActualStatus();
       boolean checkStatus = true;
       List<DishOrder> dishOrders = dishCrudOperations.findByOrderId(dishOrder.getOrder().getId());
       dishOrders.forEach(dishOrder1 -> dishOrder1.setOrder(dishOrder.getOrder()));
       for (DishOrder dishOrderTmp : dishOrders) {
           if (currentStatus != dishOrderTmp.getOrder().getActualStatus()) {
               checkStatus = false;
           }
       }

       System.out.println("dish orders : " + dishOrders);
       if(checkStatus){
           System.out.println("passed here");
           Order order = dishOrders.getFirst().getOrder();
           order.setStatus(dishOrders.getFirst().getStatus());
           System.out.println(dishOrders.getFirst().getStatus());
           System.out.println("orders to save status : " + order);
           orderCrudOperations.saveOrderStatus(order);
       }

      return dishOrder;
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


public List<DishBestSale> getBestSales(String startDate, String endDate, int limit){
        List<Order> orders = orderCrudOperations.getOrderSales(startDate, endDate);
    List<DishBestSale> dishBestSales = new ArrayList<>();
    Map<Long, DishBestSale> dishMap = new HashMap<>();

    for (Order order : orders) {
        for (DishOrder dishOrder : order.getDishOrders()) {
            Long dishId = dishOrder.getDish().getId();

            // Utilisation d'une Map pour éviter les doublons
            DishBestSale existingDish = dishMap.get(dishId);

            if (existingDish != null) {
                // Mise à jour des valeurs existantes
                existingDish.setQuantity(existingDish.getQuantity() + dishOrder.getQuantity());
                existingDish.setAmountTotal(existingDish.getAmountTotal() +
                        (dishOrder.getDish().getPrice() * dishOrder.getQuantity()));
            } else {
                // Création d'un nouveau DishBestSale
                DishBestSale newDish = new DishBestSale();
                newDish.setDish(dishCrudOperations.findByCriteria(new Criteria("id", dishId)));
                newDish.setQuantity(dishOrder.getQuantity());
                newDish.setAmountTotal(dishOrder.getDish().getPrice() * dishOrder.getQuantity());

                dishBestSales.add(newDish);
                dishMap.put(dishId, newDish);
            }
        }
    }

    dishBestSales.sort((dishBestSale1, dishBestSale2) -> (int) (dishBestSale2.getQuantity() - dishBestSale1.getQuantity()));

    if(limit > 0){
        if(dishBestSales.size()<limit) {
            System.out.println("passed here");
           return dishBestSales;
        }else{
            dishBestSales = dishBestSales.subList(0, limit);
        }
    }

    return dishBestSales;
    }
}
