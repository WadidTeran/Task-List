package org.kodigo.tasklist.menus;

import org.kodigo.tasklist.services.*;
import org.kodigo.tasklist.views.View;

public class MainMenu extends AbstractMenu {
  private final ProductivityEmailService productivityEmailService;

  public MainMenu(
      UserService userService, TaskService taskService, CategoryService categoryService) {
    super(userService, taskService, categoryService);
    title = "Task-List App";
    productivityEmailService = new ProductivityEmailService(taskService, new EmailSenderService());
  }

  @Override
  public void configureMenuOptions() {
    menuOptions.put("TASKS", 1);
    menuOptions.put("CATEGORIES", 2);
    menuOptions.put("PRODUCTIVITY REPORT", 3);
    menuOptions.put("ACCOUNT SETTINGS", 4);
  }

  @Override
  public AbstractMenu options(int optionIndex) {
    switch (optionIndex) {
      case 1 -> {
        return SingletonMenuFactory.getTaskMenu(userService, taskService, categoryService);
      }
      case 2 -> {
        return SingletonMenuFactory.getCategoryMenu(userService, taskService, categoryService);
      }
      case 3 -> {
        productivityEmailService.askForRange();
        return this;
      }
      case 4 -> {
        return SingletonMenuFactory.getAccountSettingsMenu(
            userService, taskService, categoryService);
      }
      default -> {
        return null;
      }
    }
  }

  @Override
  public AbstractMenu handleBackButton() {
    if (View.confirm("Are you sure you want to sign out?")) {
      userService.signOut();
      return SingletonMenuFactory.getLoginMenu(userService, taskService, categoryService);
    }
    return this;
  }
}
