package application;

import services.*;

import java.util.LinkedHashMap;

public class AccountSettingsMenu extends AbstractMenu {

  protected AccountSettingsMenu(
      ICRUDService crudService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    super(crudService, userService, taskService, categoryService);
    title = "Account Settings Menu";
  }

  @Override
  public void configureMenuOptions() {
    menuOptions = new LinkedHashMap<>();
    menuOptions.put("CHANGE EMAIL", 1);
    menuOptions.put("CHANGE PASSWORD", 2);
    menuOptions.put("DELETE ACCOUNT", 3);
  }

  @Override
  public AbstractMenu options(int optionIndex) {
    switch (optionIndex) {
      case 1 -> {
        userService.changeEmail();
        return this;
      }
      case 2 -> {
        userService.changePassword();
        return this;
      }
      case 3 -> {
        if (userService.deleteUser()) {
          return SingletonMenuFactory.getLoginMenu(
              crudService, userService, taskService, categoryService);
        }
        return this;
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
