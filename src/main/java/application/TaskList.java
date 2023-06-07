package application;

import controllers.CategoryController;
import controllers.TaskController;
import controllers.UserController;
import java.util.Scanner;
import models.Category;
import models.Task;
import models.User;
import repositories.CategoryRepository;
import repositories.IRepository;
import repositories.TaskRepository;
import repositories.UserRepository;
import services.CRUDServiceImpl;
import services.FilteredTaskSearchService;

public class TaskList {
  private static final Scanner scanner = new Scanner(System.in);
  private static final IRepository<User> userRepository = new UserRepository();
  private static final IRepository<Task> taskRepository = new TaskRepository();
  private static final IRepository<Category> categoryRepository = new CategoryRepository();
  private static final CRUDServiceImpl crudService =
      new CRUDServiceImpl(userRepository, taskRepository, categoryRepository);
  private static UserController userController = new UserController(crudService, scanner);
  private static CategoryController categoryController =
      new CategoryController(crudService, scanner);
  private static final FilteredTaskSearchService searchService =
      new FilteredTaskSearchService(taskRepository);
  private static TaskController taskController =
      new TaskController(crudService, searchService, scanner);

  public static void main(String[] args) {

    scanner.close();
  }
}
