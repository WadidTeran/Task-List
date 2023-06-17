package repositories;

import java.util.List;

public interface IRepository<T> {
  List<T> findAll();

  T save(T t);

  void delete(T t);
}
