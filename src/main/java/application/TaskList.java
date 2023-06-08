package application;

import controllers.CategoryController;
import controllers.TaskController;
import controllers.UserController;
import java.util.*;
import javax.swing.JOptionPane;
import models.*;
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
  private static UserController userController = new UserController(crudService);
  private static CategoryController categoryController =
      new CategoryController(crudService, scanner);
  private static final FilteredTaskSearchService searchService =
      new FilteredTaskSearchService(taskRepository);
  private static TaskController taskController =
      new TaskController(crudService, searchService, scanner);

  public static void main(String[] args) {
    Map<String, Integer> menuOptions = new HashMap<>();
    menuOptions.put("Sign In", 1);
    menuOptions.put("Sign Up", 2);
    menuOptions.put("Exit", 3);

    Object[] opArreglo = menuOptions.keySet().toArray();
    int opcionIndice = 0;

    do {
      String opcion =
          (String)
              JOptionPane.showInputDialog(
                  null,
                  "Choose an option",
                  "Task-List Login",
                  JOptionPane.INFORMATION_MESSAGE,
                  null,
                  opArreglo,
                  opArreglo[0]);
      if (opcion == null) {
        JOptionPane.showMessageDialog(null, "You have to choose an option!");
      } else {
        opcionIndice = menuOptions.get(opcion);

        switch (opcionIndice) {
          case 1 -> userController.signIn();
          case 2 -> userController.signUp();
          case 3 -> opcionIndice =
              (JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?") == 0) ? 4 : 0;
        }
      }
    } while (opcionIndice != 4);
    scanner.close();
  }
}
