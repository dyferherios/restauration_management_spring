package hei.school.course.repository.mapper;

import hei.school.course.entity.Price;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class PriceMapper {
    public Price map(ResultSet resultSet) throws SQLException {
        Price price = new Price();
        price.setId(resultSet.getLong("id"));
        price.setAmount(resultSet.getDouble("amount"));
        price.setDateValue(resultSet.getDate("date_value").toLocalDate());
        return price;
    }
}
