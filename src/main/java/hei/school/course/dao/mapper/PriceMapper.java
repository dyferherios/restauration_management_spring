package hei.school.course.dao.mapper;

import hei.school.course.model.Price;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.function.Function;

@Component
public class PriceMapper implements Function<ResultSet, Price> {
    @SneakyThrows
    @Override
    public Price apply(ResultSet resultSet) {
        Price price = new Price();
        price.setId(resultSet.getLong("id"));
        price.setAmount(resultSet.getDouble("amount"));
        price.setDateValue(resultSet.getDate("date_value").toLocalDate());
        return price;
    }
}
