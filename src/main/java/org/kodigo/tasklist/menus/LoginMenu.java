package org.kodigo.tasklist.menus;

import org.kodigo.tasklist.services.CategoryService;
import org.kodigo.tasklist.services.TaskService;
import org.kodigo.tasklist.services.UserService;
import org.kodigo.tasklist.views.View;

public class LoginMenu extends AbstractMenu {

  public LoginMenu(
          UserService userService, TaskService taskService, CategoryService categoryService) {
    super(userService, taskService, categoryService);
    title = "Task-List Login";
  }

  @Override
  public void configureMenuOptions() {
    menuOptions.put("SIGN IN", 1);
    menuOptions.put("SIGN UP", 2);
  }

  @Override
  public AbstractMenu options(int optionIndex) {
    switch (optionIndex) {
      case 1 -> {
        if (userService.signIn())
          return SingletonMenuFactory.getMainMenu(userService, taskService, categoryService);
      }
      case 2 -> userService.signUp();
      default -> {
        return null;
      }
    }
    return this;
  }

  @Override
  public AbstractMenu handleBackButton() {
    return View.confirm("Are you sure you want to exit?") ? null : this;
  }
}
