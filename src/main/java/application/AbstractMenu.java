package application;

import services.*;
import views.View;

import java.util.Map;

public abstract class AbstractMenu {
  protected final ICRUDService crudService;
  protected final UserService userService;
  protected final CategoryService categoryService;
  protected final TaskService taskService;
  protected Map<String, Integer> menuOptions;
  protected String title;

  protected AbstractMenu(
      ICRUDService crudService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    this.crudService = crudService;
    this.userService = userService;
    this.taskService = taskService;
    this.categoryService = categoryService;
    configureMenuOptions();
  }

  public AbstractMenu showMenu() {
    Object[] optionsArray = menuOptions.keySet().toArray();
    int optionIndex;

    String opcion = View.inputOptions(title, "Choose an option", optionsArray);
    if (opcion == null) {
      if (View.confirm("Are you sure you want to exit?")) return null;
    } else {
      optionIndex = menuOptions.get(opcion);
      return options(optionIndex);
    }
    return null;
  }

  public abstract void configureMenuOptions();

  public abstract AbstractMenu options(int optionIndex);
}
