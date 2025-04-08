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
}
