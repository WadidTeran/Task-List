package application;

import java.util.HashMap;
import services.*;

public class TaskMenu extends AbstractMenu {
  public TaskMenu(
      CRUDServiceImpl crudService,
      FilteredTaskSearchService searchService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    super(crudService, searchService, userService, taskService, categoryService);
    title = "Task Options";
  }
  @Override
  public void configureMenuOptions() {
    menuOptions = new HashMap<>();
    menuOptions.put("Pending Tasks", 1);
    menuOptions.put("Search Tasks", 2);
    menuOptions.put("Create Task", 3);
    menuOptions.put("Modify Task", 4);
    menuOptions.put("Delete Task", 5);
    menuOptions.put("Completed Tasks", 6);
    menuOptions.put("Back", 7);
  }

  @Override
  public AbstractMenu options(int optionIndex) {
    switch (optionIndex) {
      case 1 -> {
        return SingletonMenuFactory.getPendingTasksMenu(
            crudService, searchService, userService, taskService, categoryService);
      }
      case 2 -> {
        return SingletonMenuFactory.getSearchTasksMenu(
            crudService, searchService, userService, taskService, categoryService);
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
            crudService, searchService, userService, taskService, categoryService);
      }
      case 7 -> {
        return SingletonMenuFactory.getMainMenu(crudService, searchService, userService, taskService, categoryService);
      }
      default -> {
        return null;
      }
    }
  }
}
