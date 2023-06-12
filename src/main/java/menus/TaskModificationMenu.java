package menus;

import services.CategoryService;
import services.TaskService;
import services.UserService;
import views.View;

public class TaskModificationMenu extends AbstractMenu {

  protected TaskModificationMenu(
      UserService userService, TaskService taskService, CategoryService categoryService) {
    super(userService, taskService, categoryService);
    title = "Task modification menu";
  }

  @Override
  public void configureMenuOptions() {
    menuOptions.put("CHANGE NAME", 1);
    menuOptions.put("CHANGE DUE DATE", 2);
    menuOptions.put("CHANGE SPECIFIED TIME", 3);
    menuOptions.put("CHANGE DESCRIPTION", 4);
    menuOptions.put("CHANGE RELEVANCE", 5);
    menuOptions.put("CHANGE CATEGORY", 6);
    menuOptions.put("CHANGE REPEAT CONFIG", 7);
    menuOptions.put("CONFIRM CHANGES", 8);
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
          return SingletonMenuFactory.getModificationRepeatConfigMenu(
              userService, taskService, categoryService);
        }
      }
      case 8 -> {
        if (taskService.finishProcess("update")) {
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
