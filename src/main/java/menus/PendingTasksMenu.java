package menus;

import services.UserService;
import services.CategoryService;
import services.TaskService;

public class PendingTasksMenu extends AbstractMenu {
  public PendingTasksMenu(
      UserService userService, TaskService taskService, CategoryService categoryService) {
    super(userService, taskService, categoryService);
    title = "Pending Tasks Menu";
  }

  @Override
  public void configureMenuOptions() {
    menuOptions.put("SET A TASK AS COMPLETED", 1);
    menuOptions.put("VIEW PENDING TASKS FOR TODAY", 2);
    menuOptions.put("VIEW FUTURE PENDING TASKS", 3);
    menuOptions.put("VIEW PREVIOUS PENDING TASKS", 4);
    menuOptions.put("VIEW ALL PENDING TASKS", 5);
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
      default -> {
        return null;
      }
    }
  }

  @Override
  public AbstractMenu handleBackButton() {
    return SingletonMenuFactory.getTaskMenu(userService, taskService, categoryService);
  }
}
