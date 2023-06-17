package menus;

import services.UserService;
import services.CategoryService;
import services.TaskService;

public class TaskMenu extends AbstractMenu {
  public TaskMenu(
      UserService userService, TaskService taskService, CategoryService categoryService) {
    super(userService, taskService, categoryService);
    title = "Task Menu";
  }

  @Override
  public void configureMenuOptions() {
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
        return SingletonMenuFactory.getPendingTasksMenu(userService, taskService, categoryService);
      }
      case 2 -> {
        return SingletonMenuFactory.getSearchTasksMenu(userService, taskService, categoryService);
      }
      case 3 -> {
        taskService.createTask();
        return SingletonMenuFactory.getTaskCreationMenu(userService, taskService, categoryService);
      }
      case 4 -> {
        if (taskService.modifyTask()) {
          return SingletonMenuFactory.getTaskModificationMenu(
              userService, taskService, categoryService);
        }
      }
      case 5 -> taskService.deleteTask();
      case 6 -> {
        return SingletonMenuFactory.getCompletedTasksMenu(
            userService, taskService, categoryService);
      }
      default -> {
        return null;
      }
    }
    return this;
  }

  @Override
  public AbstractMenu handleBackButton() {
    return SingletonMenuFactory.getMainMenu(userService, taskService, categoryService);
  }
}
