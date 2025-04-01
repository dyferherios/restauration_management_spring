package hei.school.course.entity;

import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Getter
@Setter
public class StockMovement {
    private Long id;
    private Ingredient ingredient;
    private Double quantity;
    private Unit unit;
    private StockMovementType movementType;
    private Instant creationDatetime;

    public StockMovement(Ingredient ingredient, Double quantity, Unit unit, StockMovementType movementType, Instant creationDatetime) {
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.unit = unit;
        this.movementType = movementType;
        this.creationDatetime = creationDatetime;
    }
}
