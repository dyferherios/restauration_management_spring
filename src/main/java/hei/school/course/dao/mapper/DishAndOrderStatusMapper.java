package hei.school.course.dao.mapper;

import hei.school.course.model.DishAndOrderStatus;
import hei.school.course.model.Status;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.function.Function;

@Component
public class DishAndOrderStatusMapper implements Function<ResultSet, DishAndOrderStatus> {

    @SneakyThrows
    @Override
    public DishAndOrderStatus apply(ResultSet resultSet) {
        DishAndOrderStatus dishAndOrderStatus = new DishAndOrderStatus();
        dishAndOrderStatus.setId(resultSet.getLong("id"));
        dishAndOrderStatus.setStatus(Status.valueOf(resultSet.getString("status")));
        System.out.println(resultSet);
        dishAndOrderStatus.setDateValue((resultSet.getTimestamp("creation_date")).toInstant());
        return dishAndOrderStatus;
    }
}
