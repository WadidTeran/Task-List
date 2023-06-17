package repositories;

import java.util.List;
import java.util.Optional;

public interface IRepository<T> {
  List<T> findAll();

  Optional<T> getById(Long id);

  T save(T t);

  void delete(T t);
}
