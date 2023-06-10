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
  private static final ICRUDService crudService =
      new CRUDServiceImpl(userRepository, taskRepository, categoryRepository);
  private static final UserService userService = new UserService(crudService);
  private static final CategoryService categoryService = new CategoryService(crudService);
  private static final TaskService taskService = new TaskService(crudService, categoryService);

  private Application() {}

  public static void start() {
    createTestData();
    AbstractMenu currentMenu =
        new LoginMenu(crudService, userService, taskService, categoryService);
    while (currentMenu != null) {
      currentMenu = currentMenu.showMenu();
    }
  }

  private static void createTestData() {
    User user = new User("admin", "secret");

    Category category1 = new Category("Football", user);
    Category category2 = new Category("Study", user);
    Category category3 = new Category("Chess Tournament", user);

    crudService.saveUser(user);

    crudService.saveCategory(category1);
    crudService.saveCategory(category2);
    crudService.saveCategory(category3);
  }
}
