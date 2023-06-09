package application;

import java.util.HashMap;
import services.*;

public class AccountSettingsMenu extends AbstractMenu {

  protected AccountSettingsMenu(
      CRUDServiceImpl crudService,
      FilteredTaskSearchService searchService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    super(crudService, searchService, userService, taskService, categoryService);
    title = "Account Settings Menu";
  }

  @Override
  public void configureMenuOptions() {
    menuOptions = new HashMap<>();
    menuOptions.put("Change Email", 1);
    menuOptions.put("Change Password", 2);
    menuOptions.put("Delete Account", 3);
    menuOptions.put("Back", 4);
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
        userService.deleteUser();
        return this;
      }
      case 4 -> {
        return SingletonMenuFactory.getMainMenu(
            crudService, searchService, userService, taskService, categoryService);
      }
      default -> {
        return null;
      }
    }
  }
}
