package application;

import java.util.LinkedHashMap;

import services.*;

public class TaskMenu extends AbstractMenu {
  public TaskMenu(
      ICRUDService crudService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    super(crudService, userService, taskService, categoryService);
    title = "Task Menu";
  }

  @Override
  public void configureMenuOptions() {
    menuOptions = new LinkedHashMap<>();
    menuOptions.put("PENDING TASKS", 1);
    menuOptions.put("SEARCH TASKS", 2);
    menuOptions.put("CREATE TASK", 3);
    menuOptions.put("MODIFY TASK", 4);
    menuOptions.put("DELETE TASK", 5);
    menuOptions.put("COMPLETED TASKS", 6);
  }

  @Override
  public AbstractMenu options(int optionIndex) {
    switch (optionIndex) {
      case 1 -> {
        return SingletonMenuFactory.getPendingTasksMenu(
            crudService, userService, taskService, categoryService);
      }
      case 2 -> {
        return SingletonMenuFactory.getSearchTasksMenu(
            crudService, userService, taskService, categoryService);
      }
      case 3 -> {
        taskService.createTask();
        return this;
      }
      case 4 -> {
        taskService.modifyTask();
        return this;
      }
      case 5 -> {
        taskService.deleteTask();
        return this;
      }
      case 6 -> {
        return SingletonMenuFactory.getCompletedTasksMenu(
            crudService, userService, taskService, categoryService);
      }
      default -> {
        return null;
      }
    }
  }

  @Override
  public AbstractMenu handleBackButton() {
    return SingletonMenuFactory.getMainMenu(crudService, userService, taskService, categoryService);
  }
}
