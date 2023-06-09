package application;

import java.util.HashMap;
import services.*;

public class MainMenu extends AbstractMenu {

  public MainMenu(
      CRUDServiceImpl crudService,
      FilteredTaskSearchService searchService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    super(crudService, searchService, userService, taskService, categoryService);
    title = "Task-List App";
  }

  @Override
  public void configureMenuOptions() {
    menuOptions = new HashMap<>();
    menuOptions.put("Tasks", 1);
    menuOptions.put("Categories", 2);
    menuOptions.put("Account Settings", 3);
    menuOptions.put("Sign Out", 4);
  }

  @Override
  public AbstractMenu options(int optionIndex) {
    switch (optionIndex) {
      case 1 -> {
        return new TaskMenu(crudService, searchService, userService, taskService, categoryService);
      }
      case 2 -> {
        return new CategoryMenu(crudService, searchService, userService, taskService, categoryService);
      }
      case 3 -> {
        return new AccontSettingsMenu(crudService, searchService, userService, taskService, categoryService);
      }
      case 4 -> {
        userService.signOut();
        return new LoginMenu(crudService, searchService, userService, taskService, categoryService);
      }
      default -> {
        return null;
      }
    }
  }
}