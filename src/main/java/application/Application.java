package application;

import models.Category;
import models.Task;
import models.User;
import repositories.CategoryRepository;
import repositories.IRepository;
import repositories.TaskRepository;
import repositories.UserRepository;
import services.*;

public class Application {

  private static final IRepository<User> userRepository = new UserRepository();
  private static final IRepository<Task> taskRepository = new TaskRepository();
  private static final IRepository<Category> categoryRepository = new CategoryRepository();
  private static final CRUDServiceImpl crudService =
      new CRUDServiceImpl(userRepository, taskRepository, categoryRepository);
  private static final UserService userService = new UserService(crudService);
  private static final CategoryService categoryService = new CategoryService(crudService);
  private static final FilteredTaskSearchService searchService =
      new FilteredTaskSearchService(taskRepository);
  private static final TaskService taskService = new TaskService(crudService, searchService);

  private Application() {}

  public static void start() {
    AbstractMenu currentMenu =
        new LoginMenu(crudService, searchService, userService, taskService, categoryService);
    while (currentMenu != null) {
      currentMenu = currentMenu.showMenu();
    }
  }
}
