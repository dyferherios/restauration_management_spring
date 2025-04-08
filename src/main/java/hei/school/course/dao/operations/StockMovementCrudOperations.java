package hei.school.course.dao.operations;

import hei.school.course.dao.Datasource;
import hei.school.course.dao.mapper.StockMovementMapper;
import hei.school.course.model.StockMovement;
import hei.school.course.service.exception.ServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.time.Instant.now;

@Repository
public class StockMovementCrudOperations implements CrudOperations<StockMovement> {
    @Autowired
    private Datasource dataSource;
    @Autowired
    private StockMovementMapper stockMovementMapper;

    @Override
    public List<StockMovement> getAll(int page, int size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public StockMovement findById(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<StockMovement> saveAll(List<StockMovement> entities) {
        List<StockMovement> stockMovements = new ArrayList<>();
        String sqlWithId = """
                insert into stock_movement (id, quantity, unit, movement_type, creation_datetime, id_ingredient)
                values (?, ?::unit, ?::stock_movement_type, ?, ?, ?)
                on conflict (id) do nothing returning id, quantity, unit, movement_type, creation_datetime, id_ingredient""";
        String sqlWithoutId = """
                insert into stock_movement (quantity, unit, movement_type, creation_datetime, id_ingredient)
                values (?, ?::unit, ?::stock_movement_type, ?, ?)
                on conflict (id) do nothing returning id, quantity, unit, movement_type, creation_datetime, id_ingredient""";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statementWithId = connection.prepareStatement(sqlWithId);
            PreparedStatement statementWithoutId = connection.prepareStatement(sqlWithoutId);
            AtomicInteger withIdCount = new AtomicInteger();
            AtomicInteger withoutIdCount = new AtomicInteger();

            entities.forEach(entityToSave -> {
                int index = 1;
                if(entityToSave.getId() != null){
                    try{
                        statementWithId.setLong(index++, entityToSave.getId());
                        statementWithId.setDouble(index++, entityToSave.getQuantity());
                        statementWithId.setString(index++, entityToSave.getUnit().name());
                        statementWithId.setString(index++, entityToSave.getMovementType().name());
                        statementWithId.setTimestamp(index++, Timestamp.from(now()));
                        statementWithId.setLong(index, entityToSave.getIngredient().getId());
                        statementWithId.addBatch();
                        withIdCount.getAndIncrement();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    try {
                        statementWithoutId.setDouble(index++, entityToSave.getQuantity());
                        statementWithoutId.setString(index++, entityToSave.getUnit().name());
                        statementWithoutId.setString(index++, entityToSave.getMovementType().name());
                        statementWithoutId.setTimestamp(index++, Timestamp.from(now()));
                        statementWithoutId.setLong(index, entityToSave.getIngredient().getId());
                        statementWithoutId.addBatch();
                        withoutIdCount.getAndIncrement();
                        System.out.println(statementWithoutId);
                    } catch (SQLException e) {
                        throw new ServerException(e);
                    }
                }
            });
            if (withIdCount.get() > 0) {
                try (ResultSet rs = statementWithId.executeQuery()) {
                    while (rs.next()) {
                        stockMovements.add(stockMovementMapper.apply(rs));
                    }
                }
            }

            if (withoutIdCount.get() > 0) {
                try (ResultSet rs = statementWithoutId.executeQuery()) {
                    while (rs.next()) {
                        stockMovements.add(stockMovementMapper.apply(rs));
                    }
                }
            }
            return stockMovements;
        } catch (SQLException e) {
            throw new ServerException(e);
        }
    }

    public List<StockMovement> findByIdIngredient(Long idIngredient) {
        List<StockMovement> stockMovements = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "select s.id, s.quantity, s.unit, s.movement_type, s.creation_datetime from stock_movement s"
                             + " join ingredient i on s.id_ingredient = i.id"
                             + " where s.id_ingredient = ?")) {
            statement.setLong(1, idIngredient);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    stockMovements.add(stockMovementMapper.apply(resultSet));
                }
                return stockMovements;
            }
        } catch (SQLException e) {
            throw new ServerException(e);
        }
    }
}
