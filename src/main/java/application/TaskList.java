package application;

import services.CategoryService;
import services.TaskService;
import services.UserService;
import java.util.HashMap;
import java.util.Map;
import models.*;
import repositories.CategoryRepository;
import repositories.IRepository;
import repositories.TaskRepository;
import repositories.UserRepository;
import services.CRUDServiceImpl;
import services.FilteredTaskSearchService;
import views.View;

public class TaskList {
  private static final IRepository<User> userRepository = new UserRepository();
  private static final IRepository<Task> taskRepository = new TaskRepository();
  private static final IRepository<Category> categoryRepository = new CategoryRepository();
  private static final CRUDServiceImpl crudService =
      new CRUDServiceImpl(userRepository, taskRepository, categoryRepository);
  private static UserService userService = new UserService(crudService);
  private static CategoryService categoryService = new CategoryService(crudService);
  private static final FilteredTaskSearchService searchService =
      new FilteredTaskSearchService(taskRepository);
  private static TaskService taskService = new TaskService(crudService, searchService);

  public static void main(String[] args) {
    Map<String, Integer> menuOptions = new HashMap<>();
    menuOptions.put("Sign In", 1);
    menuOptions.put("Sign Up", 2);

    Object[] optionsArray = menuOptions.keySet().toArray();
    int optionIndex;

    do {
      String opcion = View.inputOptions("Task-List Login", "Choose an option", optionsArray);
      if (opcion == null) {
        if (View.confirm("Are you sure you want to exit?")) break;
      } else {
        optionIndex = menuOptions.get(opcion);

        if (optionIndex == 1) {
          userService.signIn();
        } else if (optionIndex == 2) {
          userService.signUp();
        }
      }
    } while (true);
  }
}
