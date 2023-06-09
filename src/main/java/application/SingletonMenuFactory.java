package application;

import services.*;

public class SingletonMenuFactory {
  private static AbstractMenu loginMenu;
  private static AbstractMenu mainMenu;
  private static AbstractMenu taskMenu;
  private static AbstractMenu categoryMenu;
  private static AbstractMenu accountSettingsMenu;
  private static AbstractMenu completedTaskMenu;
  private static AbstractMenu searchTasksMenu;

  private SingletonMenuFactory() {}

  public static AbstractMenu getLoginMenu(
      CRUDServiceImpl crudService,
      FilteredTaskSearchService searchService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    if (loginMenu == null) {
      loginMenu =
          new LoginMenu(crudService, searchService, userService, taskService, categoryService);
    }
    return loginMenu;
  }

  public static AbstractMenu getMainMenu(
      CRUDServiceImpl crudService,
      FilteredTaskSearchService searchService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    if (mainMenu == null) {
      mainMenu =
          new MainMenu(crudService, searchService, userService, taskService, categoryService);
    }
    return mainMenu;
  }

  public static AbstractMenu getTaskMenu(
      CRUDServiceImpl crudService,
      FilteredTaskSearchService searchService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    if (taskMenu == null) {
      taskMenu =
          new TaskMenu(crudService, searchService, userService, taskService, categoryService);
    }
    return taskMenu;
  }

  public static AbstractMenu getCategoryMenu(
      CRUDServiceImpl crudService,
      FilteredTaskSearchService searchService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    if (categoryMenu == null) {
      categoryMenu =
          new CategoryMenu(crudService, searchService, userService, taskService, categoryService);
    }
    return categoryMenu;
  }

  public static AbstractMenu getAccountSettingsMenu(
      CRUDServiceImpl crudService,
      FilteredTaskSearchService searchService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    if (accountSettingsMenu == null) {
      accountSettingsMenu =
          new AccountSettingsMenu(
              crudService, searchService, userService, taskService, categoryService);
    }
    return accountSettingsMenu;
  }

  public static AbstractMenu getSearchTasksMenu(
      CRUDServiceImpl crudService,
      FilteredTaskSearchService searchService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    if (searchTasksMenu == null) {
      searchTasksMenu =
          new SearchTasksMenu(
              crudService, searchService, userService, taskService, categoryService);
    }
    return searchTasksMenu;
  }

  public static AbstractMenu getCompletedTasksMenu(
      CRUDServiceImpl crudService,
      FilteredTaskSearchService searchService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    if (completedTaskMenu == null) {
      completedTaskMenu =
          new CompletedTasksMenu(
              crudService, searchService, userService, taskService, categoryService);
    }
    return completedTaskMenu;
  }
}
