package repositories;

import java.util.ArrayList;
import java.util.Optional;

public interface IRepository<T>{
    ArrayList<T> findAll();
    Optional<T> getById(Long id);
    T save(T t);
    void delete(T t);

}
