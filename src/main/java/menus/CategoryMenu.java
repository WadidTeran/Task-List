package menus;

import services.UserService;
import services.CategoryService;
import services.TaskService;

public class CategoryMenu extends AbstractMenu {
  public CategoryMenu(
      UserService userService, TaskService taskService, CategoryService categoryService) {
    super(userService, taskService, categoryService);
    title = "Category Menu";
  }

  @Override
  public void configureMenuOptions() {
    menuOptions.put("CREATE CATEGORY", 1);
    menuOptions.put("LIST ALL CATEGORIES", 2);
    menuOptions.put("RENAME CATEGORY", 3);
    menuOptions.put("DELETE CATEGORY", 4);
  }

  @Override
  public AbstractMenu options(int optionIndex) {
    switch (optionIndex) {
      case 1 -> categoryService.createCategory();
      case 2 -> categoryService.searchCategories();
      case 3 -> categoryService.renameCategory();
      case 4 -> categoryService.deleteCategory();
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
