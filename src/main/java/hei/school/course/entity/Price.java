package hei.school.course.entity;

import lombok.*;
import java.time.LocalDate;
import static java.time.LocalDate.now;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Getter
@Setter
public class Price {
    private Long id;
    private Ingredient ingredient;
    private Double amount;
    private LocalDate dateValue;

    public Price(Double amount) {
        this.amount = amount;
        this.dateValue = now();
    }
}
