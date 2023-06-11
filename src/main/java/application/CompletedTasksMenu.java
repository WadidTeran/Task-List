package application;

import java.util.LinkedHashMap;

import services.*;

public class CompletedTasksMenu extends AbstractMenu {
  protected CompletedTasksMenu(
      ICRUDService crudService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    super(crudService, userService, taskService, categoryService);
    title = "Completed Tasks Menu";
  }

  @Override
  public void configureMenuOptions() {
    menuOptions = new LinkedHashMap<>();
    menuOptions.put("VIEW ALL COMPLETED TASKS", 1);
    menuOptions.put("SET A COMPLETED TASK AS PENDING", 2);
    menuOptions.put("DELETE ALL COMPLETED TASK", 3);
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
      default -> {
        return null;
      }
    }
  }

  @Override
  public AbstractMenu handleBackButton() {
    return SingletonMenuFactory.getTaskMenu(crudService, userService, taskService, categoryService);
  }
}
