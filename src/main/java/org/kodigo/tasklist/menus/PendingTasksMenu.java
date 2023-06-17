package org.kodigo.tasklist.menus;

import org.kodigo.tasklist.services.CategoryService;
import org.kodigo.tasklist.services.TaskService;
import org.kodigo.tasklist.services.UserService;

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
      case 1 -> taskService.setAsCompletedTask();
      case 2 -> taskService.searchPendingTasksForToday();
      case 3 -> taskService.searchFuturePendingTasks();
      case 4 -> taskService.searchPastPendingTasks();
      case 5 -> taskService.searchAllPendingTasks();
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
