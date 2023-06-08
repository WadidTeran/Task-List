package utils;

import java.util.ArrayList;
import lombok.Getter;
import models.Category;
import models.Task;
import models.User;

public class DataBase {
  private Long lastUserId;
  private Long lastTaskId;
  private Long lastCategoryId;
  @Getter private ArrayList<User> users = new ArrayList<>();
  @Getter private ArrayList<Task> tasks = new ArrayList<>();
  @Getter private ArrayList<Category> categories = new ArrayList<>();

  public DataBase() {
    lastUserId = 0L;
    lastTaskId = 0L;
    lastCategoryId = 0L;
  }

  public User insertUser(User user) {
    user.setUserId(++lastUserId);
    this.users.add(user);
    return user;
  }

  public Task insertTask(Task task) {
    task.setTaskId(++lastTaskId);
    this.tasks.add(task);
    return task;
  }

  public Category insertCategory(Category category) {
    category.setCategoryId(++lastCategoryId);
    this.categories.add(category);
    return category;
  }

  public User updateUser(User user) {
    int userId = (int) ((long) user.getUserId());

    for (int i = (userId) - 1; i > 0; i--) {
      if (i < users.size() && users.get(i) != null && users.get(i).getUserId().equals(user.getUserId())) {
        users.set(i, user);
        break;
      }
    }
    return user;
  }

  public Task updateTask(Task task) {
    int taskId = (int) ((long) task.getTaskId());
    for (int i = (taskId - 1); i > 0; i--) {
      if (i < tasks.size() && tasks.get(i) != null && tasks.get(i).getTaskId().equals(task.getTaskId())) {
        tasks.set(i, task);
        break;
      }
    }
    return task;
  }

  public Category updateCategory(Category category) {
    int categoryId = (int) ((long) category.getCategoryId());
    for (int i = (categoryId - 1); i > 0; i--) {
      if (i < categories.size() && categories.get(i) != null && categories.get(i).getCategoryId().equals(category.getCategoryId())) {
        categories.set(i, category);
        break;
      }
    }
    return category;
  }

  public void deleteUser(User user) {
    this.users.remove(user);

    tasks.removeIf(task -> task.getUser().equals(user));
    categories.removeIf(category -> category.getUser().equals(user));
  }

  public void deleteTask(Task task) {
    this.tasks.remove(task);
  }

  public void deleteCategory(Category category) {
    this.categories.remove(category);
    tasks.forEach(task -> {
      if (task.getCategory().equals(category)) {
        task.setCategory(null);
      }
    });
  }
}
