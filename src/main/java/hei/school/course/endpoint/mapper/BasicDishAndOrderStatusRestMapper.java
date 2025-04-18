package hei.school.course.endpoint.mapper;

import hei.school.course.endpoint.rest.BasicDishAndOrderStatusRest;
import hei.school.course.model.DishAndOrderStatus;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class BasicDishAndOrderStatusRestMapper {
   @SneakyThrows
   public DishAndOrderStatus toModel(BasicDishAndOrderStatusRest dishAndOrderStatusRest){
      DishAndOrderStatus dishAndOrderStatus = new DishAndOrderStatus();
      dishAndOrderStatus.setStatus(dishAndOrderStatusRest.getStatus());
      if(dishAndOrderStatusRest.getDateValue()!=null){
         dishAndOrderStatus.setDateValue(dishAndOrderStatusRest.getDateValue());
      }else{
         dishAndOrderStatus.setDateValue(Instant.now());
      }
      return dishAndOrderStatus;
   }
}
