package repositories;

import java.util.ArrayList;

public interface IRepository<T>{
    ArrayList<T> findAll();
    T getById(Long id);
    T save(T t);
    void delete(T t);

}
