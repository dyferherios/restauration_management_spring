package hei.school.course.model;

import lombok.*;
import org.springframework.stereotype.Component;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Getter
@Setter
@Component
public class DishAndOrderStatus {
    private Long id;
    private Status status;
    private Instant dateValue;
    private Order order;

    @Override
    public String toString() {
        return "DishAndOrderStatus(id=" + id +
                ", status=" + status +
                ", dateValue=" + dateValue +
                ", orderId=" + (order != null ? order.getId() : null) + ")";
    }
}
