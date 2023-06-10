package application;

import java.util.LinkedHashMap;

import services.*;

public class MainMenu extends AbstractMenu {
  public MainMenu(
      ICRUDService crudService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    super(crudService, userService, taskService, categoryService);
    title = "Task-List App";
  }

  @Override
  public void configureMenuOptions() {
    menuOptions = new LinkedHashMap<>();
    menuOptions.put("Tasks", 1);
    menuOptions.put("Categories", 2);
    menuOptions.put("Account Settings", 3);
    menuOptions.put("Sign Out", 4);
  }

  @Override
  public AbstractMenu options(int optionIndex) {
    switch (optionIndex) {
      case 1 -> {
        return SingletonMenuFactory.getTaskMenu(
            crudService, userService, taskService, categoryService);
      }
      case 2 -> {
        return SingletonMenuFactory.getCategoryMenu(
            crudService, userService, taskService, categoryService);
      }
      case 3 -> {
        return SingletonMenuFactory.getAccountSettingsMenu(
            crudService, userService, taskService, categoryService);
      }
      case 4 -> {
        userService.signOut();
        return SingletonMenuFactory.getLoginMenu(
            crudService, userService, taskService, categoryService);
      }
      default -> {
        return null;
      }
    }
  }
}
