package hei.school.course.repository.mapper;

import hei.school.course.entity.StockMovement;
import hei.school.course.entity.StockMovementType;
import hei.school.course.entity.Unit;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class StockMovementMapper {
    public StockMovement map(ResultSet resultSet) throws SQLException {
        StockMovement stockMovement = new StockMovement();
        stockMovement.setId(resultSet.getLong("id"));
        stockMovement.setQuantity(resultSet.getDouble("quantity"));
        stockMovement.setMovementType(StockMovementType.valueOf(resultSet.getString("movement_type")));
        stockMovement.setUnit(Unit.valueOf(resultSet.getString("unit")));
        stockMovement.setCreationDatetime(resultSet.getTimestamp("creation_datetime").toInstant());
        return stockMovement;
    }
}
