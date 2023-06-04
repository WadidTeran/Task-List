package repositories;

import java.util.ArrayList;
import java.util.Optional;
import lombok.Data;
import models.User;

@Data
public class UserRepository extends DataBaseRepositoryImpl implements IRepository<User> {
  @Override
  public ArrayList<User> findAll() {
    return this.connection.getUsers();
  }

  @Override
  public Optional<User> getById(Long id) {
    Optional<User> optionalUser = findAll().stream()
            .filter(p -> p.getUserId().equals(id)).findFirst();
    return optionalUser;
  }

  @Override
  public User save(User user) {
    this.connection.insertUser(user);
    return user;
  }

  @Override
  public void delete(User user) {
    this.connection.deleteUser(user);
  }
}
