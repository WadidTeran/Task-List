package services;

import java.util.ArrayList;
import models.Task;

public interface ICompletedTasksService {
  ArrayList<Task> getCompletedTasks();
}
