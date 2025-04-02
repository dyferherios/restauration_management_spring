package hei.school.course.dao.mapper;


import hei.school.course.model.StockMovement;
import hei.school.course.model.StockMovementType;
import hei.school.course.model.Unit;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.function.Function;

@Component
public class StockMovementMapper implements Function<ResultSet, StockMovement> {
    @SneakyThrows
    @Override
    public StockMovement apply(ResultSet resultSet) {
        StockMovement stockMovement = new StockMovement();
        stockMovement.setId(resultSet.getLong("id"));
        stockMovement.setQuantity(resultSet.getDouble("quantity"));
        stockMovement.setMovementType(StockMovementType.valueOf(resultSet.getString("movement_type")));
        stockMovement.setUnit(Unit.valueOf(resultSet.getString("unit")));
        stockMovement.setCreationDatetime(resultSet.getTimestamp("creation_datetime").toInstant());
        return stockMovement;
    }
}
