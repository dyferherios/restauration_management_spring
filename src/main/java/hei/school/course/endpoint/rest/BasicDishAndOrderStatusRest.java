package hei.school.course.endpoint.rest;

import hei.school.course.model.Status;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Setter
@Getter
@Component
public class BasicDishAndOrderStatusRest {
    private Status status;
    private Instant dateValue;
}
