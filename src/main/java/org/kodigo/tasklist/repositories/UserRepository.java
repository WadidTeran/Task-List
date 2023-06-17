package org.kodigo.tasklist.repositories;

import java.util.List;
import org.kodigo.tasklist.models.User;
import org.kodigo.tasklist.utils.DataBaseConnection;

public class UserRepository extends DataBaseRepositoryImpl implements IRepository<User> {

  public UserRepository() {
    setConnection(DataBaseConnection.getConnection());
  }

  @Override
  public List<User> findAll() {
    return this.connection.getUsers();
  }

  @Override
  public User save(User user) {
    if (user.getUserId() != null && user.getUserId() > 0L) {
      this.connection.updateUser(user);
    } else {
      this.connection.insertUser(user);
    }

    return user;
  }

  @Override
  public void delete(User user) {
    this.connection.deleteUser(user);
  }
}
