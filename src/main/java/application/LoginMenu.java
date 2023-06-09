package application;

import java.util.HashMap;
import services.*;

public class LoginMenu extends AbstractMenu {

  public LoginMenu(
      CRUDServiceImpl crudService,
      FilteredTaskSearchService searchService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    super(crudService, searchService, userService, taskService, categoryService);
    configureMenuOptions();
    title = "Task-List Login";
  }

  @Override
  public void configureMenuOptions() {
    menuOptions = new HashMap<>();
    menuOptions.put("Sign In", 1);
    menuOptions.put("Sign Up", 2);
  }

  @Override
  public AbstractMenu options(int optionIndex) {
    switch (optionIndex) {
      case 1 -> {
        userService.signIn();
        return new MainMenu(crudService, searchService, userService, taskService, categoryService);
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
}
