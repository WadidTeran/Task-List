package services;

import java.util.List;

import models.Category;
import models.Task;

public interface ICategoryTasksService {
  List<Task> getCategoryTasks(Category category);
}
