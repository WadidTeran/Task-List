package application;

import java.util.LinkedHashMap;

import services.*;

public class CategoryMenu extends AbstractMenu {
  public CategoryMenu(
      ICRUDService crudService,
      UserService userService,
      TaskService taskService,
      CategoryService categoryService) {
    super(crudService, userService, taskService, categoryService);
    title = "Category Menu";
  }

  @Override
  public void configureMenuOptions() {
    menuOptions = new LinkedHashMap<>();
    menuOptions.put("Create Category", 1);
    menuOptions.put("List All Categories", 2);
    menuOptions.put("Rename Category", 3);
    menuOptions.put("Delete Category", 4);
    menuOptions.put("Back", 5);
  }

  @Override
  public AbstractMenu options(int optionIndex) {
    switch (optionIndex) {
      case 1 -> {
        categoryService.createCategory();
        return this;
      }
      case 2 -> {
        categoryService.searchCategories();
        return this;
      }
      case 3 -> {
        categoryService.renameCategory();
        return this;
      }
      case 4 -> {
        categoryService.deleteCategory();
        return this;
      }
      case 5 -> {
        return SingletonMenuFactory.getMainMenu(
            crudService, userService, taskService, categoryService);
      }
      default -> {
        return null;
      }
    }
  }
}
