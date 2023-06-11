package menus;

import services.UserService;
import services.CategoryService;
import services.TaskService;
import views.View;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractMenu {
  protected final UserService userService;
  protected final CategoryService categoryService;
  protected final TaskService taskService;
  protected final Map<String, Integer> menuOptions;
  protected String title;

  protected AbstractMenu(
      UserService userService, TaskService taskService, CategoryService categoryService) {
    this.userService = userService;
    this.taskService = taskService;
    this.categoryService = categoryService;
    this.menuOptions = new LinkedHashMap<>();
    configureMenuOptions();
  }

  public AbstractMenu showMenu() {
    Object[] optionsArray = menuOptions.keySet().toArray();
    int optionIndex;

    String opcion = View.inputOptions(title, "Choose an option", optionsArray);
    if (opcion == null) {
      return handleBackButton();
    } else {
      optionIndex = menuOptions.get(opcion);
      return options(optionIndex);
    }
  }

  public abstract void configureMenuOptions();

  public abstract AbstractMenu options(int optionIndex);

  public abstract AbstractMenu handleBackButton();
}
