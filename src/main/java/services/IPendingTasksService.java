package services;

import java.util.ArrayList;
import models.Task;

public interface IPendingTasksService {
  ArrayList<Task> getFuturePendingTasks();

  ArrayList<Task> getPastPendingTasks();

  ArrayList<Task> getPendingTasksForToday();

  ArrayList<Task> getAllPendingTasks();
}
