package org.kodigo.tasklist.menus;

import org.kodigo.tasklist.services.CategoryService;
import org.kodigo.tasklist.services.TaskService;
import org.kodigo.tasklist.services.UserService;

public class SingletonMenuFactory {
  private static AbstractMenu loginMenu;
  private static AbstractMenu mainMenu;
  private static AbstractMenu taskMenu;
  private static AbstractMenu categoryMenu;
  private static AbstractMenu accountSettingsMenu;
  private static AbstractMenu completedTasksMenu;
  private static AbstractMenu searchTasksMenu;
  private static AbstractMenu pendingTasksMenu;
  private static AbstractMenu taskCreationMenu;
  private static AbstractMenu taskModificationMenu;
  private static AbstractMenu creationRepeatConfigMenu;
  private static AbstractMenu modificationRepeatConfigMenu;

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

  public static AbstractMenu getTaskCreationMenu(
      UserService userService, TaskService taskService, CategoryService categoryService) {
    if (taskCreationMenu == null) {
      taskCreationMenu = new TaskCreationMenu(userService, taskService, categoryService);
    }
    return taskCreationMenu;
  }

  public static AbstractMenu getTaskModificationMenu(
      UserService userService, TaskService taskService, CategoryService categoryService) {
    if (taskModificationMenu == null) {
      taskModificationMenu = new TaskModificationMenu(userService, taskService, categoryService);
    }
    return taskModificationMenu;
  }

  public static AbstractMenu getCreationRepeatConfigMenu(
      UserService userService, TaskService taskService, CategoryService categoryService) {
    if (creationRepeatConfigMenu == null) {
      creationRepeatConfigMenu =
          new CreationRepeatConfigMenu(userService, taskService, categoryService);
    }
    return creationRepeatConfigMenu;
  }

  public static AbstractMenu getModificationRepeatConfigMenu(
      UserService userService, TaskService taskService, CategoryService categoryService) {
    if (modificationRepeatConfigMenu == null) {
      modificationRepeatConfigMenu =
          new ModificationRepeatConfigMenu(userService, taskService, categoryService);
    }
    return modificationRepeatConfigMenu;
  }
}
