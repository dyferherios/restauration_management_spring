package hei.school.course.endpoint;
import hei.school.course.endpoint.mapper.*;
import hei.school.course.endpoint.rest.BasicDishAndOrderStatusRest;
import hei.school.course.endpoint.rest.DishBestSaleRest;
import hei.school.course.endpoint.rest.DishOrderRest;
import hei.school.course.endpoint.rest.OrderRest;
import hei.school.course.model.*;
import hei.school.course.service.OrderService;
import hei.school.course.service.exception.ClientException;
import hei.school.course.service.exception.NotFoundException;
import hei.school.course.service.exception.ServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
public class OrderRestController {
    private final OrderService orderService;
    private final OrderRestMapper orderRestMapper;
    private final DishOrderRestMapper dishOrderRestMapper;
    private final BasicDishAndOrderStatusRestMapper basicDishAndOrderStatusRestMapper;
    private final DishBestSaleRestMapper dishBestSaleRestMapper;

    @PostMapping("/orders/{reference}")
    public ResponseEntity<Object> saveAll(@PathVariable String reference) {
        try {
            OrderRest orderRest = new OrderRest();
            orderRest.setReference(reference);
            orderRest.setStatus(Status.CREATED);
            orderRest.setCreationDate(Date.from(Instant.now()));
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

    @PutMapping("/orders/{reference}")
    public ResponseEntity<Object> saveOrderStatus(@PathVariable String reference,
                                                  @RequestBody OrderRest orderRestToSave) {
        try {
            OrderRest orderRest = new OrderRest();
            orderRest.setReference(reference);
            orderRest.setStatus(orderRestToSave.getStatus());
            orderRest.setCreationDate(Date.from(Instant.now()));
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
    public ResponseEntity<Object> addDishOrder(@PathVariable String reference, @RequestBody OrderRest orderRest){
        try{
            Order order = orderService.findByReference(reference);
            List<DishOrder> dishOrders = orderRest.getDishOrderRests().stream()
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
            DishOrder dishOrder = order.getDishOrders().stream().filter(dishOrder1 -> Objects.equals(dishOrder1.getDish().getId(), dishId)).toList().get(0);
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

    @GetMapping("/orders/sales")
    public ResponseEntity<Object> getBestSales(@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate, @RequestParam int top){
        try{
            String defaultStartDate = "1999-01-01";
            String defaultEndDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
            if (startDate == null) {
                startDate = defaultStartDate;
            }
            if (endDate == null) {
                endDate = defaultEndDate;
            }

            List<DishBestSale> orders = orderService.getBestSales(startDate, endDate, top);
            List<DishBestSaleRest> orderRests = orders.stream()
                    .map(dishBestSaleRestMapper::toRest)
                    .toList();
            return ResponseEntity.ok().body(orderRests);
        }catch (ClientException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        } catch (ServerException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/orders/dishes/{dishId}/processingTime")
    public ResponseEntity<Object> getProcessingTime(@PathVariable Long dishId,
                                                    @RequestParam(required = false) String startDate,
                                                    @RequestParam(required = false) String endDate,
                                                    @RequestParam(required = false, defaultValue = "SECONDS") String durationUnit,
                                                    @RequestParam(required = false, defaultValue = "AVERAGE") String calculationMode) {
        try {
            String defaultStartDate = "1999-01-01";
            String defaultEndDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);

            if (startDate == null) {
                startDate = defaultStartDate;
            }
            if (endDate == null) {
                endDate = defaultEndDate;
            }

            List<Order> orders = orderService.getAllOrderBetweenDates(startDate, endDate);
            List<DishOrder> dishOrders = orders.stream()
                    .flatMap(order -> Optional.ofNullable(order.getDishOrders())
                            .stream()
                            .flatMap(List::stream))
                    .filter(dishOrder -> Optional.ofNullable(dishOrder.getDish())
                            .map(Dish::getId)
                            .filter(id -> id.equals(dishId))
                            .isPresent())
                    .toList();
            Double processingTime = orderService.getProcessingTimeForDish(dishOrders, durationUnit, calculationMode);
            ProcessingTimeDish processingtimeDish = new ProcessingTimeDish(
                    dishOrders.getFirst().getDish().getId(),
                    dishOrders.getFirst().getDish().getName(),
                    durationUnit,
                    calculationMode,
                    processingTime,
                    startDate,
                    endDate
            );
            return ResponseEntity.ok().body(processingtimeDish);
        } catch (ClientException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        } catch (ServerException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
