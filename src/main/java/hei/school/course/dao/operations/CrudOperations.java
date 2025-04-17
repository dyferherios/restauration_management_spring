package hei.school.course.dao.operations;

import hei.school.course.model.Criteria;

import java.util.List;

public interface CrudOperations<E> {
    List<E> getAll(int page, int size);

    E findById(Long id);

    E findByCriteria(Criteria criteria);

    // Both create (if does not exist) or update (if exist) entities
    List<E> saveAll(List<E> entities);


}
