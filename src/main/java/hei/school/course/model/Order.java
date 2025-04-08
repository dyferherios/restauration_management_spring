package hei.school.course.model;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Setter
@Getter
public class Order {
    private Long id;
    private String reference;
    private List<DishOrder> dishOrders;
    private List<DishAndOrderStatus> status;

    public Status getActualStatus() {
        List<DishAndOrderStatus> statuses = getStatus();
        return statuses.getLast().getStatus();
    }

}
