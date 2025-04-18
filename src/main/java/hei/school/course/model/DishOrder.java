package hei.school.course.model;

import lombok.*;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Getter
@Setter
@Component
@ToString(exclude={"order"})
public class DishOrder {
    private Long id;
    private Dish dish;
    private Order order;
    private Double quantity;
    private List<DishAndOrderStatus> status;

    public Duration getProcessingTime(){
        Instant dateValueStatusInProgress = status.stream().filter(status -> status.getStatus() == Status.INPROGRESS)
                .toList().getLast().getDateValue();
        Instant dateValueStatusFinished = status.stream().filter(status -> status.getStatus() == Status.FINISHED)
                .toList().getLast().getDateValue();

        return Duration.between(dateValueStatusInProgress, dateValueStatusFinished);

    }

}
