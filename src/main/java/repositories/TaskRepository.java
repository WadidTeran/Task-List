package repositories;

import java.util.ArrayList;
import java.util.Optional;
import models.Task;

public class TaskRepository extends DataBaseRepositoryImpl implements IRepository<Task> {
  @Override
  public ArrayList<Task> findAll() {
    return this.connection.getTasks();
  }

  @Override
  public Optional<Task> getById(Long id) {
    return findAll().stream().filter(t -> t.getTaskId().equals(id)).findFirst();
  }

  @Override
  public Task save(Task task) {
    this.connection.insertTask(task);
    return task;
  }

  @Override
  public void delete(Task task) {
    this.connection.deleteTask(task);
  }
}
