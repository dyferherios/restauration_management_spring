package hei.school.course.endpoint;
import hei.school.course.endpoint.mapper.OrderRestMapper;
import hei.school.course.endpoint.rest.OrderRest;
import hei.school.course.model.Order;
import hei.school.course.service.OrderService;
import hei.school.course.service.exception.ClientException;
import hei.school.course.service.exception.NotFoundException;
import hei.school.course.service.exception.ServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
public class OrderRestController {
    private final OrderService orderService;
    private final OrderRestMapper orderRestMapper;

    @PostMapping("/orders")
    public ResponseEntity<Object> saveAll(@RequestBody OrderRest orderRest) {
        try {
            Order order = orderRestMapper.toModel(orderRest);
            Order createdOrder = orderService.saveAll(order);
            OrderRest createdOrderRest = orderRestMapper.toRest(createdOrder);
            return ResponseEntity.ok().body(createdOrderRest);
        } catch (ClientException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
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
}
