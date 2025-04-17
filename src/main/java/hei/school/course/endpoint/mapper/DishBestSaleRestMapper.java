package hei.school.course.endpoint.mapper;

import hei.school.course.endpoint.rest.DishBestSaleRest;
import hei.school.course.model.DishBestSale;
import org.springframework.stereotype.Component;

@Component
public class DishBestSaleRestMapper {
    public DishBestSaleRest toRest(DishBestSale dishBestSale){
        return new DishBestSaleRest(
                dishBestSale.getDish().getId(),
                dishBestSale.getDish().getName(),
                dishBestSale.getQuantity(),
                dishBestSale.getAmountTotal());
    }
}
