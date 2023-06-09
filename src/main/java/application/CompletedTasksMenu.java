package application;

import java.util.LinkedHashMap;

import services.*;

public class CompletedTasksMenu extends AbstractMenu {
  protected CompletedTasksMenu(
      CRUDServiceImpl crudService,
      FilteredTaskSearchService searchService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    super(crudService, searchService, userService, taskService, categoryService);
    title = "Completed Tasks Menu";
  }

  @Override
  public void configureMenuOptions() {
    menuOptions = new LinkedHashMap<>();
    menuOptions.put("View All Completed Tasks", 1);
    menuOptions.put("Set A Completed Task As Pending", 2);
    menuOptions.put("Delete All Completed Task", 3);
    menuOptions.put("Back", 4);
  }

  @Override
  public AbstractMenu options(int optionIndex) {
    switch (optionIndex) {
      case 1 -> {
        taskService.searchCompletedTasks();
        return this;
      }
      case 2 -> {
        taskService.setAsPendingTask();
        return this;
      }
      case 3 -> {
        taskService.deleteCompletedTasks();
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
