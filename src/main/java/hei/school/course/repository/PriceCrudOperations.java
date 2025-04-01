package hei.school.course.repository;

import hei.school.course.entity.Criteria;
import hei.school.course.configuration.Datasource;
import hei.school.course.entity.Price;
import hei.school.course.repository.mapper.PriceMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
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
public class PriceCrudOperations implements CrudOperations<Price> {
    private final Datasource dataSource;
    private PriceMapper priceMapper;

    @Override
    public List<Price> getAll(int page, int size) {
        List<Price> prices = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "select p.id, p.amount, p.date_value from price limit ? offset ?;")){
            statement.setInt(1, size);
            statement.setInt(2, (page-1)*size);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                prices.add(priceMapper.map(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return prices;
    }

    @Override
    public Price findById(Long id) {
        Price price;
        try(Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "select p.id, p.amount, p.date_value from price p where p.id = ?")){
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            price = priceMapper.map(resultSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return price;
    }

    public List<Price> filterByIngredientIdAndCriteria(Long ingredientId, List<Criteria> criterias, Map<String, String> sort){
        List<Price> prices = new ArrayList<>();
        StringBuilder query = new StringBuilder("select distinct p.id, p.id_ingredient, p.amount, p.date_value from price p ");
        List<String> criteriaKey = criterias.stream()
                .map(Criteria::getKey)
                .toList();
        List<String> targetKeys = List.of("amount", "date");
        if (criteriaKey.stream().anyMatch(targetKeys::contains)) {
            query.append(" where ");
            for (Criteria criteria : criterias) {
                StringBuilder conditions = new StringBuilder();
                if (criteria.getValue() instanceof Date || criteria.getValue() instanceof LocalDate) {
                    conditions.append("p.date_value").append(" ").append(criteria.getOperation()).append(" '").append(criteria.getValue()).append("' ");
                    query.append(conditions).append(" ").append(criteria.getConjunction().toLowerCase()).append(" ");
                } else if ("amount".equals(criteria.getKey())) {
                    conditions.append("p.amount").append(" ").append(criteria.getOperation()).append(" ").append(criteria.getValue());
                    query.append(conditions).append(" ").append(criteria.getConjunction().toLowerCase()).append(" ");
                }
            }
            query.append(" p.id_ingredient=").append(ingredientId);
        }else{
            query.append(" where p.id_ingredient=").append(ingredientId);
        }

        if (!sort.isEmpty()) {
            query.append(" order by ");
            List<String> orderClauses = sort.entrySet().stream()
                    .map(entry -> "p." + entry.getKey() + " " + entry.getValue())
                    .toList();
            query.append(String.join(", ", orderClauses));
        }

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query.toString());
            while (resultSet.next()) {
                prices.add(priceMapper.map(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return prices;
    }

    @Override
    public List<Price> filterByCriteria(List<Criteria> criterias, int page, int size, Map<String, String> sort) {
        List<Price> prices = new ArrayList<>();
        StringBuilder query = new StringBuilder("select p.id, p.amount, p.date_value, p.id_ingredient from p where 1=1 ");
        if(!criterias.isEmpty()){
            criterias.forEach(criteria -> {
                String params = Stream.of(criteria.getConjunction(), criteria.getKey(), criteria.getOperation(), criteria.getValue())
                        .map(String::valueOf)
                        .collect(Collectors.joining(" "));
                query.append(params);
            });
        }
        if(!sort.isEmpty()){
            query.append(" order by ");
            List<String> params = sort.entrySet().stream()
                    .map(entry -> "p." + entry.getKey() + " " + entry.getValue())
                    .toList();
            query.append(String.join(", ", params));
        }
        query.append(" limit ").append(page).append(" offset ").append((page-1)*size);
        try(Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(query.toString());
            while(resultSet.next()){
                prices.add(priceMapper.map(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return prices;
    }

    @SneakyThrows
    @Override
    public List<Price> saveAll(List<Price> entities) {
        List<Price> prices = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement("insert into price (id, amount, date_value, id_ingredient) values (?, ?, ?, ?)"
                             + " on conflict (id) do nothing"
                             + " returning id, amount, date_value, id_ingredient");) {
            entities.forEach(entityToSave -> {
                try {
                    statement.setLong(1, entityToSave.getId());
                    statement.setDouble(2, entityToSave.getAmount());
                    statement.setDate(3, Date.valueOf(entityToSave.getDateValue()));
                    statement.setLong(4, entityToSave.getIngredient().getId());
                    statement.addBatch();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    prices.add(priceMapper.map(resultSet));
                }
            }
            return prices;
        }
    }

    public List<Price> findByIdIngredient(Long idIngredient) {
        List<Price> prices = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select p.id, p.amount, p.date_value from price p"
                     + " join ingredient i on p.id_ingredient = i.id"
                     + " where p.id_ingredient = ?")) {
            statement.setLong(1, idIngredient);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Price price = priceMapper.map(resultSet);
                    prices.add(price);
                }
                return prices;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
