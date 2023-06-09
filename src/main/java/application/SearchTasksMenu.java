package application;

import services.*;

import java.util.LinkedHashMap;

public class SearchTasksMenu extends AbstractMenu {
  protected SearchTasksMenu(
      CRUDServiceImpl crudService,
      FilteredTaskSearchService searchService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    super(crudService, searchService, userService, taskService, categoryService);
    title = "Search Tasks Menu";
  }

  @Override
  public void configureMenuOptions() {
    menuOptions = new LinkedHashMap<>();
    menuOptions.put("Search a specific task", 1);
    menuOptions.put("Search tasks by category", 2);
    menuOptions.put("Search tasks by relevance", 3);
    menuOptions.put("Back", 4);
  }

  @Override
  public AbstractMenu options(int optionIndex) {
    switch (optionIndex) {
      case 1 -> {
        taskService.searchOneTask();
        return this;
      }
      case 2 -> {
        taskService.searchTasksByCategory();
        return this;
      }
      case 3 -> {
        taskService.searchTasksByRelevance();
        return this;
      }
      case 4 -> {
        return SingletonMenuFactory.getTaskMenu(
            crudService, searchService, userService, taskService, categoryService);
      }
      default -> {
        return null;
      }
    }
  }
}
