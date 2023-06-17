package org.kodigo.tasklist.menus;

import org.kodigo.tasklist.services.CategoryService;
import org.kodigo.tasklist.services.TaskService;
import org.kodigo.tasklist.services.UserService;

public class AccountSettingsMenu extends AbstractMenu {

  protected AccountSettingsMenu(
          UserService userService, TaskService taskService, CategoryService categoryService) {
    super(userService, taskService, categoryService);
    title = "Account Settings Menu";
  }

  @Override
  public void configureMenuOptions() {
    menuOptions.put("CHANGE EMAIL", 1);
    menuOptions.put("CHANGE PASSWORD", 2);
    menuOptions.put("DELETE ACCOUNT", 3);
  }

  @Override
  public AbstractMenu options(int optionIndex) {
    switch (optionIndex) {
      case 1 -> userService.changeEmail();
      case 2 -> userService.changePassword();
      case 3 -> {
        if (userService.deleteUser()) {
          return SingletonMenuFactory.getLoginMenu(userService, taskService, categoryService);
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
    return SingletonMenuFactory.getMainMenu(userService, taskService, categoryService);
  }
}
