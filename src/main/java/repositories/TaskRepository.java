package repositories;

import java.util.ArrayList;
import java.util.Optional;
import lombok.Data;
import models.Task;

@Data
public class TaskRepository extends DataBaseRepositoryImpl implements IRepository<Task> {
  @Override
  public ArrayList<Task> findAll() {
    return this.connection.getTasks();
  }

  @Override
  public Optional<Task> getById(Long id) {
    Optional<Task> optionalTask = findAll().stream()
            .filter(p -> p.getTaskId().equals(id)).findFirst();
    return optionalTask;
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