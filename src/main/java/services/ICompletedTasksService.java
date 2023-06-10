package services;

import java.util.List;
import models.Task;

public interface ICompletedTasksService {
  List<Task> getCompletedTasks();
}
