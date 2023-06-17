package org.kodigo.tasklist.repositories;

import java.util.List;
import java.util.stream.Collectors;
import org.kodigo.tasklist.models.Task;
import org.kodigo.tasklist.utils.DataBaseConnection;
import org.kodigo.tasklist.utils.UserLogin;

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
