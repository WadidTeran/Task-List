package services;

import java.util.ArrayList;
import java.util.Optional;

import lombok.AllArgsConstructor;
import models.Category;
import models.Task;
import models.User;
import repositories.IRepository;

@AllArgsConstructor
public class CRUDServiceImpl implements ICRUDService {
  private IRepository<User> userRepository;
  private IRepository<Task> taskRepository;
  private IRepository<Category> categoryRepository;

  @Override
  public ArrayList<User> findAllUsers() {
    return userRepository.findAll();
  }

  @Override
  public ArrayList<Task> findAllTasks() {
    return taskRepository.findAll();
  }

  @Override
  public ArrayList<Category> findAllCategories() {
    return categoryRepository.findAll();
  }

  @Override
  public User saveUser(User user) {
    return userRepository.save(user);
  }

  @Override
  public Task saveTask(Task task) {
    return taskRepository.save(task);
  }

  @Override
  public Category saveCategory(Category category) {
    return categoryRepository.save(category);
  }

  @Override
  public void deleteUser(User user) {
    userRepository.delete(user);
  }

  @Override
  public void deleteTask(Task task) {
    taskRepository.delete(task);
  }

  @Override
  public void deleteCategory(Category category) {
    categoryRepository.delete(category);
  }

  @Override
  public Optional<User> getUserById(Long id) {
    return userRepository.getById(id);
  }

  @Override
  public Optional<Task> getTaskById(Long id) {
    return taskRepository.getById(id);
  }

  @Override
  public Optional<Category> getCategoryById(Long id) {
    return categoryRepository.getById(id);
  }

  @Override
  public void deleteCompletedTasks() {
    ICompletedTasksService completedTasksService = new FilteredTaskSearchService(taskRepository);
    ArrayList<Task> completedTasks = completedTasksService.getCompletedTasks();

    for (Task completedTask : completedTasks) {
      taskRepository.delete(completedTask);
    }
  }

}
