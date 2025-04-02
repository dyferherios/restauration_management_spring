package hei.school.course.endpoint.mapper;



import hei.school.course.endpoint.rest.StockMovementRest;
import hei.school.course.model.StockMovement;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class StockMovementRestMapper implements Function<StockMovement, StockMovementRest> {

    @Override
    public StockMovementRest apply(StockMovement stockMovement) {
        return new StockMovementRest(stockMovement.getId(),
                stockMovement.getQuantity(),
                stockMovement.getUnit(),
                stockMovement.getMovementType(),
                stockMovement.getCreationDatetime());
    }
}
