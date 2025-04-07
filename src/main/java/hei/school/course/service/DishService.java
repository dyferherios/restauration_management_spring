package hei.school.course.service;

import hei.school.course.dao.operations.DishCrudOperations;
import hei.school.course.model.Dish;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DishService {
    private final DishCrudOperations dishCrudOperations;

    public List<Dish> getAll(int page, int size){
        return dishCrudOperations.getAll(page, size);
    }
}
