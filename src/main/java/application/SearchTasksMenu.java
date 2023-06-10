package application;

import services.*;

import java.util.LinkedHashMap;

public class SearchTasksMenu extends AbstractMenu {
  protected SearchTasksMenu(
      ICRUDService crudService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    super(crudService, userService, taskService, categoryService);
    title = "Search Tasks Menu";
  }

  @Override
  public void configureMenuOptions() {
    menuOptions = new LinkedHashMap<>();
    menuOptions.put("SEARCH A SPECIFIC TASK", 1);
    menuOptions.put("SEARCH TASKS BY CATEGORY", 2);
    menuOptions.put("SEARCH TASKS BY RELEVANCE", 3);
    menuOptions.put("BACK", 4);
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
            crudService, userService, taskService, categoryService);
      }
      default -> {
        return null;
      }
    }
  }
}
