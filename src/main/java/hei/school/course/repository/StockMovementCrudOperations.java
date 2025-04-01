package hei.school.course.repository;

import hei.school.course.entity.Criteria;
import hei.school.course.configuration.Datasource;
import hei.school.course.entity.*;
import hei.school.course.repository.mapper.StockMovementMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@AllArgsConstructor
public class StockMovementCrudOperations implements CrudOperations<StockMovement> {
    private final Datasource dataSource;
    private final StockMovementMapper stockMovementMapper;

    @Override
    public List<StockMovement> getAll(int page, int size) {
        try(Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("select id, quantity, unit, movement_type, creation_datetime from stock_movement limit ? offset ?")) {
            statement.setInt(1, size);
            statement.setInt(2, size*(page-1));
            ResultSet resultSet = statement.executeQuery();
            List<StockMovement> stockMovements = new ArrayList<>();
            while(resultSet.next()){
                stockMovements.add(stockMovementMapper.map(resultSet));
            }
            return stockMovements;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StockMovement findById(Long id) {
        StockMovement stockMovement = null;
        try(Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("select id, quantity, unit, movement_type, creation_datetime from stock_movement where id=?")) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                stockMovement = stockMovementMapper.map(resultSet);
            }
            return stockMovement;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<StockMovement> filterByCriteria(List<Criteria> criterias, int page, int size, Map<String, String> sort) {
        List<StockMovement> stockMovements = new ArrayList<>();
        StringBuilder query = new StringBuilder("select id, quantity, unit, movement_type, creation_datetime from stock_movement where 1=1 ");
        if(!criterias.isEmpty()){
            criterias.forEach(criteria -> {
                if("unit".equals(criteria.getKey()) || "movement_type".equals(criteria.getKey())){
                    query.append(criteria.getConjunction()).append(" ").append(criteria.getKey()).append("::TEXT ").append(criteria.getOperation()).append(" '%").append(criteria.getValue()).append("%' ").append(" ");
                }else{
                    List<String> params = Stream.of(criteria.getConjunction(), criteria.getKey(), criteria.getOperation(), criteria.getValue())
                            .map(String::valueOf).toList();
                    query.append(String.join(", ", params));
                }
            });
        }
        if(!sort.isEmpty()){
            List<String> order = sort.entrySet().stream()
                    .map(entry -> entry.getKey() + " " + entry.getValue())
                    .toList();
            query.append(String.join(", ", order));
        }
        query.append(" limit ").append(size).append(" offset ").append((page-1)*size);
        try(Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(query.toString());
            while (resultSet.next()){
                stockMovements.add(stockMovementMapper.map(resultSet));
            }
            return stockMovements;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<StockMovement> filterByIngredientIdByCriteria(Long ingredientId, List<Criteria> criterias, Map<String, String> sort) {
        List<StockMovement> movements = new ArrayList<>();
        StringBuilder query = new StringBuilder("select distinct sm.id, sm.movement_type, sm.quantity, sm.creation_datetime, sm.unit from stock_movement sm where 1=1 ");
        List<String> criteriaKey = criterias.stream()
                .map(Criteria::getKey)
                .toList();
        List<String> targetKeys = List.of("movement_type", "quantity", "date", "unit");
        if (criteriaKey.stream().anyMatch(targetKeys::contains)) {
            for (Criteria criteria : criterias) {
                if (criteria.getValue() instanceof Date || criteria.getValue() instanceof LocalDate) {
                    query.append(criteria.getConjunction().toLowerCase()).append(" ").append("sm.creation_datetime").append(" ").append(criteria.getOperation()).append(" '").append(criteria.getValue()).append("'");
                } else if(criteria.getValue() instanceof String) {
                    if("movement_type".equals(criteria.getKey()) || "unit".equals(criteria.getKey())){
                        query.append(criteria.getConjunction().toLowerCase()).append(" ").append("sm.").append(criteria.getKey()).append("::TEXT ").append(criteria.getOperation()).append(" '%").append(criteria.getValue()).append("%' ").append(" ");
                    }
                } else if ("quantity".equals(criteria.getKey())) {
                    query.append(criteria.getConjunction().toLowerCase()).append(" ").append("sm.").append(criteria.getKey()).append(" ").append(criteria.getOperation()).append(" ").append(criteria.getValue());
                }
            }
        }
        query.append(" sm.id_ingredient=").append(ingredientId);
        if (!sort.isEmpty()) {
            query.append(" order by ");
            List<String> orderClauses = sort.entrySet().stream()
                    .map(entry -> "sm." + entry.getKey() + " " + entry.getValue())
                    .toList();
            query.append(String.join(", ", orderClauses));
        }

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
             ResultSet resultSet = statement.executeQuery(query.toString());
            while (resultSet.next()) {
                movements.add(stockMovementMapper.map(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return movements;
    }

    @Override
    public List<StockMovement> saveAll(List<StockMovement> entities) {
        List<StockMovement> stockMovements = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            for (StockMovement entityToSave : entities) {
                String sql;
                if (entityToSave.getId() == null) {
                    sql = "INSERT INTO stock_movement (quantity, unit, movement_type, creation_datetime, id_ingredient) " +
                            "VALUES (?, ?::unit, ?::stock_movement_type, ?, ?) " +
                            "RETURNING id, quantity, unit, movement_type, creation_datetime, id_ingredient;";
                } else {
                    sql = "INSERT INTO stock_movement (id, quantity, unit, movement_type, creation_datetime, id_ingredient) " +
                            "VALUES (?, ?, ?::unit, ?::stock_movement_type, ?, ?) " +
                            "ON CONFLICT (id) DO NOTHING " +
                            "RETURNING id, quantity, unit, movement_type, creation_datetime, id_ingredient;";
                    }

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    int parameterIndex = 1;
                    if (entityToSave.getId() != null) {
                        statement.setLong(parameterIndex++, entityToSave.getId());
                    }
                    statement.setDouble(parameterIndex++, entityToSave.getQuantity());
                    statement.setString(parameterIndex++, entityToSave.getUnit().name());
                    statement.setString(parameterIndex++, entityToSave.getMovementType().name());
                    statement.setTimestamp(parameterIndex++, Timestamp.from(entityToSave.getCreationDatetime()));
                    statement.setLong(parameterIndex, entityToSave.getIngredient().getId());

                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            StockMovement savedStockMovement = stockMovementMapper.map(resultSet);
                            stockMovements.add(savedStockMovement);
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return stockMovements;
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
                    stockMovements.add(stockMovementMapper.map(resultSet));
                }
                return stockMovements;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
