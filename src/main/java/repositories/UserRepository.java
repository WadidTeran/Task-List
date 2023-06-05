package repositories;

import java.util.ArrayList;
import java.util.Optional;
import models.User;

public class UserRepository extends DataBaseRepositoryImpl implements IRepository<User> {
  @Override
  public ArrayList<User> findAll() {
    return this.connection.getUsers();
  }

  @Override
  public Optional<User> getById(Long id) {
    return findAll().stream().filter(u -> u.getUserId().equals(id)).findFirst();
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
