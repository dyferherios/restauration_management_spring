package hei.school.course.dao.operations;


import hei.school.course.dao.Datasource;
import hei.school.course.dao.mapper.PriceMapper;
import hei.school.course.model.Price;
import hei.school.course.service.exception.ServerException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PriceCrudOperations implements CrudOperations<Price> {
    @Autowired
    private Datasource dataSource;
    @Autowired
    private PriceMapper priceMapper;

    @Override
    public List<Price> getAll(int page, int size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Price findById(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @SneakyThrows
    @Override
    public List<Price> saveAll(List<Price> entities) {
        List<Price> prices = new ArrayList<>();
        String sqlWithId = "insert into price (id, amount, date_value, id_ingredient) values (?, ?, ?, ?)"
                + " on conflict (id) do nothing"
                + " returning id, amount, date_value, id_ingredient";
        String sqlWithoutId = "insert into price (amount, date_value, id_ingredient) values (?, ?, ?)"
                + " on conflict (id) do nothing"
                + " returning id, amount, date_value, id_ingredient";

        try (Connection connection = dataSource.getConnection();
             ) {
            PreparedStatement statementWithId = connection.prepareStatement(sqlWithId);
            PreparedStatement statementWithoutId = connection.prepareStatement(sqlWithoutId);
            entities.forEach(entityToSave -> {
                int index = 1;
                if(entityToSave.getId() != null ){
                    try {
                        statementWithId.setLong(index++, entityToSave.getId());
                        statementWithId.setDouble(index++, entityToSave.getAmount());
                        statementWithId.setDate(index++, Date.valueOf(entityToSave.getDateValue()));
                        statementWithId.setLong(index, entityToSave.getIngredient().getId());
                        System.out.println(statementWithId);
                        try (ResultSet resultSet = statementWithId.executeQuery()) {
                            if (resultSet.next()) {
                                prices.add(priceMapper.apply(resultSet));
                            }
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    try {
                        statementWithoutId.setDouble(index++, entityToSave.getAmount());
                        statementWithoutId.setDate(index++, Date.valueOf(entityToSave.getDateValue()));
                        statementWithoutId.setLong(index, entityToSave.getIngredient().getId());
                        System.out.println(statementWithoutId);
                        try (ResultSet resultSet = statementWithoutId.executeQuery()) {
                            if (resultSet.next()) {
                                prices.add(priceMapper.apply(resultSet));
                            }
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
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
                    Price price = priceMapper.apply(resultSet);
                    prices.add(price);
                }
                return prices;
            }
        } catch (SQLException e) {
            throw new ServerException(e);
        }
    }
}
