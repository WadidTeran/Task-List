package services;

import java.util.List;
import models.Task;

public interface IPendingTasksService {
  List<Task> getFuturePendingTasks();

  List<Task> getPastPendingTasks();

  List<Task> getPendingTasksForToday();

  List<Task> getAllPendingTasks();
}
