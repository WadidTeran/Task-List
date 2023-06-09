package application;

import java.util.HashMap;
import services.*;

public class PendingTasksMenu extends AbstractMenu {
  public PendingTasksMenu(
      CRUDServiceImpl crudService,
      FilteredTaskSearchService searchService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    super(crudService, searchService, userService, taskService, categoryService);
    title = "Pending Tasks Menu";
  }

  @Override
  public void configureMenuOptions() {
    menuOptions = new HashMap<>();
    menuOptions.put("Set A Task As Completed", 1);
    menuOptions.put("View Pending Tasks For Today", 2);
    menuOptions.put("View Future Pending Tasks", 3);
    menuOptions.put("View Previous Pending Tasks", 4);
    menuOptions.put("View All Pending Tasks", 5);
    menuOptions.put("Back", 6);
  }

  @Override
  public AbstractMenu options(int optionIndex) {
    switch (optionIndex) {
      case 1 -> {
        taskService.setAsCompletedTask();
        return this;
      }
      case 2 -> {
        taskService.searchPendingTasksForToday();
        return this;
      }
      case 3 -> {
        taskService.searchFuturePendingTasks();
        return this;
      }
      case 4 -> {
        taskService.searchPastPendingTasks();
        return this;
      }
      case 5 -> {
        taskService.searchAllPendingTasks();
        return this;
      }
      case 6 -> {
        return SingletonMenuFactory.getTaskMenu(
            crudService, searchService, userService, taskService, categoryService);
      }
      default -> {
        return null;
      }
    }
  }
}
