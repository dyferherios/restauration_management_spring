package hei.school.course.endpoint.mapper;

import hei.school.course.endpoint.rest.BasicDishAndOrderStatusRest;
import hei.school.course.model.DishAndOrderStatus;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class BasicDishAndOrderStatusRestMapper {
   @SneakyThrows
   public DishAndOrderStatus toModel(BasicDishAndOrderStatusRest dishAndOrderStatusRest){
      DishAndOrderStatus dishAndOrderStatus = new DishAndOrderStatus();
      dishAndOrderStatus.setStatus(dishAndOrderStatusRest.getStatus());
      dishAndOrderStatus.setDateValue(dishAndOrderStatusRest.getDateValue());
      return dishAndOrderStatus;
   }
}
