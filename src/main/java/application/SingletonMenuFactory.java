package application;

import services.*;

public class SingletonMenuFactory {
  private static AbstractMenu loginMenu;
  private static AbstractMenu mainMenu;
  private static AbstractMenu taskMenu;
  private static AbstractMenu categoryMenu;
  private static AbstractMenu accountSettingsMenu;
  private static AbstractMenu completedTasksMenu;
  private static AbstractMenu searchTasksMenu;
  private static AbstractMenu pendingTasksMenu;

  private SingletonMenuFactory() {}

  public static AbstractMenu getLoginMenu(
      ICRUDService crudService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    if (loginMenu == null) {
      loginMenu = new LoginMenu(crudService, userService, taskService, categoryService);
    }
    return loginMenu;
  }

  public static AbstractMenu getMainMenu(
      ICRUDService crudService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    if (mainMenu == null) {
      mainMenu = new MainMenu(crudService, userService, taskService, categoryService);
    }
    return mainMenu;
  }

  public static AbstractMenu getTaskMenu(
      ICRUDService crudService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    if (taskMenu == null) {
      taskMenu = new TaskMenu(crudService, userService, taskService, categoryService);
    }
    return taskMenu;
  }

  public static AbstractMenu getCategoryMenu(
      ICRUDService crudService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    if (categoryMenu == null) {
      categoryMenu = new CategoryMenu(crudService, userService, taskService, categoryService);
    }
    return categoryMenu;
  }

  public static AbstractMenu getAccountSettingsMenu(
      ICRUDService crudService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    if (accountSettingsMenu == null) {
      accountSettingsMenu =
          new AccountSettingsMenu(crudService, userService, taskService, categoryService);
    }
    return accountSettingsMenu;
  }

  public static AbstractMenu getSearchTasksMenu(
      ICRUDService crudService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    if (searchTasksMenu == null) {
      searchTasksMenu = new SearchTasksMenu(crudService, userService, taskService, categoryService);
    }
    return searchTasksMenu;
  }

  public static AbstractMenu getCompletedTasksMenu(
      ICRUDService crudService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    if (completedTasksMenu == null) {
      completedTasksMenu =
          new CompletedTasksMenu(crudService, userService, taskService, categoryService);
    }
    return completedTasksMenu;
  }

  public static AbstractMenu getPendingTasksMenu(
      ICRUDService crudService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    if (pendingTasksMenu == null) {
      pendingTasksMenu =
          new PendingTasksMenu(crudService, userService, taskService, categoryService);
    }
    return pendingTasksMenu;
  }
}
