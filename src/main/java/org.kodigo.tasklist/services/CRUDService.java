package services;

import java.util.List;
import models.Category;
import models.Task;
import models.User;
import repositories.IRepository;

public class CRUDService {
  private final IRepository<User> userRepository;
  private final IRepository<Task> taskRepository;
  private final IRepository<Category> categoryRepository;

  public CRUDService(
      IRepository<User> userRepository,
      IRepository<Task> taskRepository,
      IRepository<Category> categoryRepository) {
    this.userRepository = userRepository;
    this.taskRepository = taskRepository;
    this.categoryRepository = categoryRepository;
  }

  public List<User> findAllUsers() {
    return userRepository.findAll();
  }

  public List<Task> findAllTasks() {
    return taskRepository.findAll();
  }

  public List<Category> findAllCategories() {
    return categoryRepository.findAll();
  }

  public User saveUser(User user) {
    return userRepository.save(user);
  }

  public Task saveTask(Task task) {
    return taskRepository.save(task);
  }

  public Category saveCategory(Category category) {
    return categoryRepository.save(category);
  }

  public void deleteUser(User user) {
    userRepository.delete(user);
  }

  public void deleteTask(Task task) {
    taskRepository.delete(task);
  }

  public void deleteCategory(Category category) {
    categoryRepository.delete(category);
  }
}
