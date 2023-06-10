package services;

import java.util.ArrayList;
import java.util.Optional;
import models.Category;
import models.Task;
import models.User;

public interface ICRUDService {
  ArrayList<User> findAllUsers();

  ArrayList<Task> findAllTasks();

  ArrayList<Category> findAllCategories();

  User saveUser(User user);

  Task saveTask(Task task);

  Category saveCategory(Category category);

  void deleteUser(User user);

  void deleteTask(Task task);

  void deleteCategory(Category category);

  Optional<User> getUserById(Long id);

  Optional<Task> getTaskById(Long id);

  Optional<Category> getCategoryById(Long id);
}
