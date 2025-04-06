package hei.school.course.endpoint.mapper;


import hei.school.course.endpoint.rest.PriceRest;
import hei.school.course.model.Price;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class PriceRestMapper implements Function<Price, PriceRest> {

    @Override
    public PriceRest apply(Price price) {
        return new PriceRest(price.getId(), price.getAmount(), price.getDateValue());
    }

    public Price toModel(PriceRest priceRest){
        Price price = new Price();
        if(priceRest.getId()!=null){
            price.setId(priceRest.getId());
        }
        price.setAmount(priceRest.getPrice());
        price.setDateValue(priceRest.getDateValue());
        return price;
    }
}
