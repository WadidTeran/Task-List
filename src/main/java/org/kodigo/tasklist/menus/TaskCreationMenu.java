package org.kodigo.tasklist.menus;

import org.kodigo.tasklist.services.CategoryService;
import org.kodigo.tasklist.services.TaskService;
import org.kodigo.tasklist.services.UserService;
import org.kodigo.tasklist.views.View;

public class TaskCreationMenu extends AbstractMenu {
  protected TaskCreationMenu(
          UserService userService, TaskService taskService, CategoryService categoryService) {
    super(userService, taskService, categoryService);
    title = "Task creation menu";
  }

  @Override
  public void configureMenuOptions() {
    menuOptions.put("SET A NAME", 1);
    menuOptions.put("SET A DUE DATE", 2);
    menuOptions.put("SET A SPECIFIED TIME", 3);
    menuOptions.put("SET A DESCRIPTION", 4);
    menuOptions.put("SET A RELEVANCE", 5);
    menuOptions.put("SET A CATEGORY", 6);
    menuOptions.put("SET A REPEAT CONFIG", 7);
    menuOptions.put("CREATE", 8);
  }

  @Override
  public AbstractMenu options(int optionIndex) {
    switch (optionIndex) {
      case 1 -> taskService.processName();
      case 2 -> taskService.processDueDate();
      case 3 -> taskService.processSpecifiedTime();
      case 4 -> taskService.processDescription();
      case 5 -> taskService.processRelevance();
      case 6 -> taskService.processCategory();
      case 7 -> {
        if (taskService.processRepeatConfig()) {
          return SingletonMenuFactory.getCreationRepeatConfigMenu(
              userService, taskService, categoryService);
        }
      }
      case 8 -> {
        if (taskService.finishProcess("create")) {
          return SingletonMenuFactory.getTaskMenu(userService, taskService, categoryService);
        }
      }
      default -> {
        return null;
      }
    }
    return this;
  }

  @Override
  public AbstractMenu handleBackButton() {
    if (View.confirm("Are you sure you want to cancel?")) {
      return SingletonMenuFactory.getTaskMenu(userService, taskService, categoryService);
    }
    return this;
  }
}
