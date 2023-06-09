package services;

import java.util.ArrayList;
import models.Category;
import utils.UserLogin;
import views.View;

public class CategoryService {
  public static final String NO_CATEGORIES_WARNING = "You don't have any categories created yet.";
  private static final int MAX_CATEGORY_NAME_LENGTH = 50;
  private static final int MAXIMUM_CATEGORIES = 10;
  private static final String EXISTING_CATEGORY_WARNING = "This category already exists!";
  private static final String LONG_CATEGORY_NAME_WARNING =
      "Category names cannot be longer than " + MAX_CATEGORY_NAME_LENGTH + " characters!";
  private static final String MAXIMUM_CATEGORIES_WARNING =
      "You cannot create more than " + MAXIMUM_CATEGORIES + " categories!";
  private final CRUDServiceImpl crudService;

  public CategoryService(CRUDServiceImpl crudService) {
    this.crudService = crudService;
  }

  public void createCategory() {
    if (crudService.findAllCategories().size() >= MAXIMUM_CATEGORIES) {
      View.message(MAXIMUM_CATEGORIES_WARNING);
    } else {
      String newCategory = View.input("Insert the new category's name: ");
      if (newCategory != null) {
        if (newCategory.length() > MAX_CATEGORY_NAME_LENGTH) {
          View.message(LONG_CATEGORY_NAME_WARNING);
        } else if (newCategory.isBlank() || newCategory.isEmpty()) {
          View.message("Not a valid name!");
        } else if (crudService.checkCategoryName(newCategory)) {
          View.message(EXISTING_CATEGORY_WARNING);
        } else {
          crudService.saveCategory(new Category(newCategory, UserLogin.getUser()));
          View.message("Category \"" + newCategory + "\" created succesfully.");
        }
      }
    }
  }

  public void renameCategory() {
    ArrayList<Category> categories = crudService.findAllCategories();
    if (categories.isEmpty()) {
      View.message(NO_CATEGORIES_WARNING);
    } else {
      Object[] categoriesArray = categories.stream().map(Category::getName).toArray();

      String category =
          View.inputOptions("Category selector", "Choose the category to rename", categoriesArray);

      if (category != null) {
        if (!crudService.checkCategoryName(category)) {
          View.message("The category " + category + " doesn't exist.");
        } else {
          String newCategory = View.input("Insert a new name for the category: ");
          if (newCategory != null) {
            if (newCategory.length() > MAX_CATEGORY_NAME_LENGTH) {
              View.message(LONG_CATEGORY_NAME_WARNING);
            } else if (crudService.checkCategoryName(newCategory)) {
              View.message(EXISTING_CATEGORY_WARNING);
            } else if (View.confirm(
                "Are you sure you want to rename \""
                    + category
                    + "\" category to \""
                    + newCategory
                    + "\"?")) {
              Category oldCategory = crudService.getCategoryByName(category);
              oldCategory.setName(newCategory);
              crudService.saveCategory(oldCategory);
            }
          }
        }
      }
    }
  }

  public void deleteCategory() {
    ArrayList<Category> categories = crudService.findAllCategories();
    if (categories.isEmpty()) {
      View.message(NO_CATEGORIES_WARNING);
    } else {
      Object[] categoriesArray = categories.stream().map(Category::getName).toArray();

      String category =
          View.inputOptions("Category selector", "Choose the category to delete", categoriesArray);

      if (category != null) {
        if (!crudService.checkCategoryName(category)) {
          View.message("The category " + category + " doesn't exist.");
        } else if (View.confirm("Are you sure you want to delete \"" + category + "\" category?")) {
          Category categoryToDelete = crudService.getCategoryByName(category);
          crudService.deleteCategory(categoryToDelete);
          View.message("Category \"" + categoryToDelete + "\" deleted succesfully.");
        }
      }
    }
  }

  public void searchCategories() {
    ArrayList<Category> categories = crudService.findAllCategories();
    if (categories.isEmpty()) {
      View.message(NO_CATEGORIES_WARNING);
    } else {
      View.displayCategories(categories);
    }
  }
}
