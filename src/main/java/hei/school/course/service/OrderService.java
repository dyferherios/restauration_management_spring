package hei.school.course.service;

import hei.school.course.dao.operations.OrderCrudOperations;
import hei.school.course.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
   private final OrderCrudOperations orderCrudOperations;

   public Order findByReference(String orderReference){
       return orderCrudOperations.findByReference(orderReference);
   }
}
