package menus;

import services.UserService;
import services.CategoryService;
import services.TaskService;

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
      UserService userService, TaskService taskService, CategoryService categoryService) {
    if (loginMenu == null) {
      loginMenu = new LoginMenu(userService, taskService, categoryService);
    }
    return loginMenu;
  }

  public static AbstractMenu getMainMenu(
      UserService userService, TaskService taskService, CategoryService categoryService) {
    if (mainMenu == null) {
      mainMenu = new MainMenu(userService, taskService, categoryService);
    }
    return mainMenu;
  }

  public static AbstractMenu getTaskMenu(
      UserService userService, TaskService taskService, CategoryService categoryService) {
    if (taskMenu == null) {
      taskMenu = new TaskMenu(userService, taskService, categoryService);
    }
    return taskMenu;
  }

  public static AbstractMenu getCategoryMenu(
      UserService userService, TaskService taskService, CategoryService categoryService) {
    if (categoryMenu == null) {
      categoryMenu = new CategoryMenu(userService, taskService, categoryService);
    }
    return categoryMenu;
  }

  public static AbstractMenu getAccountSettingsMenu(
      UserService userService, TaskService taskService, CategoryService categoryService) {
    if (accountSettingsMenu == null) {
      accountSettingsMenu = new AccountSettingsMenu(userService, taskService, categoryService);
    }
    return accountSettingsMenu;
  }

  public static AbstractMenu getSearchTasksMenu(
      UserService userService, TaskService taskService, CategoryService categoryService) {
    if (searchTasksMenu == null) {
      searchTasksMenu = new SearchTasksMenu(userService, taskService, categoryService);
    }
    return searchTasksMenu;
  }

  public static AbstractMenu getCompletedTasksMenu(
      UserService userService, TaskService taskService, CategoryService categoryService) {
    if (completedTasksMenu == null) {
      completedTasksMenu = new CompletedTasksMenu(userService, taskService, categoryService);
    }
    return completedTasksMenu;
  }

  public static AbstractMenu getPendingTasksMenu(
      UserService userService, TaskService taskService, CategoryService categoryService) {
    if (pendingTasksMenu == null) {
      pendingTasksMenu = new PendingTasksMenu(userService, taskService, categoryService);
    }
    return pendingTasksMenu;
  }
}
