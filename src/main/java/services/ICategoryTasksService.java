package services;

import java.util.ArrayList;
import models.Category;
import models.Task;

public interface ICategoryTasksService {
  ArrayList<Task> getCategoryTasks(Category category);
}
