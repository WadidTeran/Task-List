package application;

import java.util.LinkedHashMap;
import services.*;
import views.View;

public class LoginMenu extends AbstractMenu {

  public LoginMenu(
      ICRUDService crudService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    super(crudService, userService, taskService, categoryService);
    configureMenuOptions();
    title = "Task-List Login";
  }

  @Override
  public void configureMenuOptions() {
    menuOptions = new LinkedHashMap<>();
    menuOptions.put("SIGN IN", 1);
    menuOptions.put("SIGN UP", 2);
  }

  @Override
  public AbstractMenu options(int optionIndex) {
    switch (optionIndex) {
      case 1 -> {
        if (userService.signIn()) {
          return SingletonMenuFactory.getMainMenu(
              crudService, userService, taskService, categoryService);
        }
        return this;
      }
      case 2 -> {
        userService.signUp();
        return this;
      }
      default -> {
        return null;
      }
    }
  }

  @Override
  public AbstractMenu handleBackButton() {
    return View.confirm("Are you sure you want to exit?") ? null : this;
  }
}
