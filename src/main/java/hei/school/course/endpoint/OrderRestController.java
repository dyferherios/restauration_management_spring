package hei.school.course.endpoint;
import hei.school.course.endpoint.mapper.BasicDishAndOrderStatusRestMapper;
import hei.school.course.endpoint.mapper.DishAndOrderStatusRestMapper;
import hei.school.course.endpoint.mapper.DishOrderRestMapper;
import hei.school.course.endpoint.mapper.OrderRestMapper;
import hei.school.course.endpoint.rest.BasicDishAndOrderStatusRest;
import hei.school.course.endpoint.rest.DishOrderRest;
import hei.school.course.endpoint.rest.OrderRest;
import hei.school.course.model.DishAndOrderStatus;
import hei.school.course.model.DishOrder;
import hei.school.course.model.Order;
import hei.school.course.service.OrderService;
import hei.school.course.service.exception.ClientException;
import hei.school.course.service.exception.NotFoundException;
import hei.school.course.service.exception.ServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
public class OrderRestController {
    private final OrderService orderService;
    private final OrderRestMapper orderRestMapper;
    private final DishOrderRestMapper dishOrderRestMapper;
    private final BasicDishAndOrderStatusRestMapper basicDishAndOrderStatusRestMapper;

    @PostMapping("/orders")
    public ResponseEntity<Object> saveAll(@RequestBody OrderRest orderRest) {
        try {
            Order order = orderRestMapper.toModel(orderRest);
            Order createdOrder = orderService.saveAll(order);
            OrderRest createdOrderRest = orderRestMapper.toRest(createdOrder);
            return ResponseEntity.ok().body(createdOrderRest);
        } catch (ClientException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        } catch (ServerException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/orders/{reference}")
    public ResponseEntity<Object> getByReference(@PathVariable String reference){
        try{
            Order order = orderService.findByReference(reference);
            OrderRest orderRest = orderRestMapper.toRest(order);
            return  ResponseEntity.ok().body(orderRest);
        }catch (ClientException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        } catch (ServerException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/orders/{reference}/dishes")
    public ResponseEntity<Object> addDishOrder(@PathVariable String reference, @RequestBody List<DishOrderRest> dishOrderRests){
        try{
            Order order = orderService.findByReference(reference);
            List<DishOrder> dishOrders = dishOrderRests.stream()
                    .map(dishOrderRestMapper::toModel)
                    .toList();
            order.setDishOrders(dishOrders);
            Order updatedOrder = orderService.addDishOrder(order);
            OrderRest updatedOrderRest = orderRestMapper.toRest(updatedOrder);
            return ResponseEntity.ok().body(updatedOrderRest);
        }catch (ClientException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        } catch (ServerException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/orders/{reference}/dishes/{dishId}")
    public ResponseEntity<Object> updateDishOrderStatus(@PathVariable String reference, @PathVariable Long dishId, @RequestBody BasicDishAndOrderStatusRest dishAndOrderStatusRest){
        try{
            Order order = orderService.findByReference(reference);
            DishAndOrderStatus dishAndOrderStatus = basicDishAndOrderStatusRestMapper.toModel(dishAndOrderStatusRest);
            DishOrder dishOrder = order.getDishOrders().stream().filter(dishOrder1 -> Objects.equals(dishOrder1.getDish().getId(), dishId)).toList().getFirst();
            dishOrder.setStatus(List.of(dishAndOrderStatus));
            dishOrder.setOrder(order);
            DishOrder updatedDishOrder = orderService.updateDishOrderStatus(dishOrder);
            DishOrderRest updatedDishOrderRest = dishOrderRestMapper.apply(updatedDishOrder);
            return ResponseEntity.ok().body(updatedDishOrderRest);
        }catch (ClientException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        } catch (ServerException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
