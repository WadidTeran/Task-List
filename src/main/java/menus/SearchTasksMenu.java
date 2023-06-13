package menus;

import services.UserService;
import services.CategoryService;
import services.TaskService;

public class SearchTasksMenu extends AbstractMenu {
  protected SearchTasksMenu(
      UserService userService, TaskService taskService, CategoryService categoryService) {
    super(userService, taskService, categoryService);
    title = "Search Tasks Menu";
  }

  @Override
  public void configureMenuOptions() {
    menuOptions.put("SEARCH A SPECIFIC TASK", 1);
    menuOptions.put("SEARCH TASKS BY CATEGORY", 2);
    menuOptions.put("SEARCH TASKS BY RELEVANCE", 3);
  }

  @Override
  public AbstractMenu options(int optionIndex) {
    switch (optionIndex) {
      case 1 -> taskService.searchOneTask();
      case 2 -> taskService.searchTasksByCategory();
      case 3 -> taskService.searchTasksByRelevance();
      default -> {
        return null;
      }
    }
    return this;
  }

  @Override
  public AbstractMenu handleBackButton() {
    return SingletonMenuFactory.getTaskMenu(userService, taskService, categoryService);
  }
}
