package menus;

import services.CategoryService;
import services.TaskService;
import services.UserService;

public class CreationRepeatConfigMenu extends AbstractMenu {

  protected CreationRepeatConfigMenu(
      UserService userService, TaskService taskService, CategoryService categoryService) {
    super(userService, taskService, categoryService);
    title = "Repeat config menu";
  }

  @Override
  public void configureMenuOptions() {
    menuOptions.put("REPEAT TYPE", 1);
    menuOptions.put("REPEAT ENDS AT", 2);
    menuOptions.put("REPEAT INTERVAL", 3);
    menuOptions.put("REPEAT ON", 4);
  }

  @Override
  public AbstractMenu options(int optionIndex) {
    switch (optionIndex) {
      case 1 -> taskService.processRepeatType();
      case 2 -> taskService.processRepeatEndsAt();
      case 3 -> taskService.processRepeatInterval();
      case 4 -> taskService.processRepeatOn();
      default -> {
        return null;
      }
    }
    return this;
  }

  @Override
  public AbstractMenu handleBackButton() {
    if (taskService.finishRepeatConfigProcess()) {
      return SingletonMenuFactory.getTaskCreationMenu(userService, taskService, categoryService);
    }
    return this;
  }
}
