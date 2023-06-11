package application;

import java.util.LinkedHashMap;

import services.*;
import views.View;

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
    menuOptions.put("TASKS", 1);
    menuOptions.put("CATEGORIES", 2);
    menuOptions.put("ACCOUNT SETTINGS", 3);
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
      default -> {
        return null;
      }
    }
  }

  @Override
  public AbstractMenu handleBackButton() {
    if (View.confirm("Are you sure you want to sign out?")) {
      userService.signOut();
      return SingletonMenuFactory.getLoginMenu(
          crudService, userService, taskService, categoryService);
    }
    return this;
  }
}
