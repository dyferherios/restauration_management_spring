package hei.school.course.model;

import lombok.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    private Date creationDate;

    public Status getActualStatus() {
        List<DishAndOrderStatus> statuses = getStatus();
        return statuses.get(statuses.size()-1).getStatus();
    }


    public Optional<Duration> getProcessingDurationForDish(Long dishId) {
        Optional<Instant> inProgressTime = getStatusTime(dishId, Status.INPROGRESS);
        Optional<Instant> finishedTime = getStatusTime(dishId, Status.FINISHED);

        if (inProgressTime.isPresent() && finishedTime.isPresent()) {
            return Optional.of(Duration.between(inProgressTime.get(), finishedTime.get()));
        }
        return Optional.empty();
    }

    private Optional<Instant> getStatusTime(Long dishId, Status status) {
        return dishOrders.stream()
                .filter(dishOrder -> dishOrder.getDish().getId().equals(dishId))
                .flatMap(dishOrder -> dishOrder.getStatus().stream())
                .filter(statusHistory -> statusHistory.getStatus() == status)
                .map(DishAndOrderStatus::getDateValue)
                .min(Comparator.naturalOrder()); // Prend le plus ancien pour IN_PROGRESS
    }
}
