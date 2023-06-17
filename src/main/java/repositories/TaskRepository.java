package repositories;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import models.Task;
import utils.DataBaseConnection;
import utils.UserLogin;

public class TaskRepository extends DataBaseRepositoryImpl implements IRepository<Task> {

  public TaskRepository() {
    setConnection(DataBaseConnection.getConnection());
  }

  @Override
  public List<Task> findAll() {
    return this.connection.getTasks().stream()
        .filter(t -> t.getUser().equals(UserLogin.getLoggedUser()))
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Task> getById(Long id) {
    return findAll().stream()
        .filter(t -> t.getTaskId().equals(id) && t.getUser().equals(UserLogin.getLoggedUser()))
        .findFirst();
  }

  @Override
  public Task save(Task task) {
    if (task.getTaskId() != null && task.getTaskId() > 0L) {
      this.connection.updateTask(task);
    } else {
      this.connection.insertTask(task);
    }

    return task;
  }

  @Override
  public void delete(Task task) {
    if (task.getUser().equals(UserLogin.getLoggedUser())) {
      this.connection.deleteTask(task);
    }
  }
}
