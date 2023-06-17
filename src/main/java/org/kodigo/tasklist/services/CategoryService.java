package org.kodigo.tasklist.services;

import java.util.List;
import java.util.Optional;
import org.kodigo.tasklist.models.Category;
import org.kodigo.tasklist.utils.UserLogin;
import org.kodigo.tasklist.utils.Warnings;
import org.kodigo.tasklist.views.View;

public class CategoryService {
  private static final int MAX_CATEGORY_NAME_LENGTH = 50;
  private static final int MAXIMUM_CATEGORIES = 10;
  private static final String LONG_CATEGORY_NAME_WARNING =
      "Category names cannot be longer than " + MAX_CATEGORY_NAME_LENGTH + " characters!";
  private static final String MAXIMUM_CATEGORIES_WARNING =
      "You cannot create more than " + MAXIMUM_CATEGORIES + " categories!";
  private final CRUDService crudService;

  public CategoryService(CRUDService crudService) {
    this.crudService = crudService;
  }

  public void createCategory() {
    if (crudService.findAllCategories().size() >= MAXIMUM_CATEGORIES) {
      View.message(MAXIMUM_CATEGORIES_WARNING);
    } else {
      String newCategory = View.input("Insert the new category's name: ");
      if (newCategory == null) return;

      if (newCategory.isBlank() || newCategory.isEmpty()) {
        View.message(Warnings.EMPTY_INPUT);
      } else {
        newCategory = newCategory.trim();
        if (newCategory.length() > MAX_CATEGORY_NAME_LENGTH) {
          View.message(LONG_CATEGORY_NAME_WARNING);
        } else if (newCategory.isBlank() || newCategory.isEmpty()) {
          View.message(Warnings.EMPTY_INPUT);
        } else if (checkCategoryName(newCategory)) {
          View.message(Warnings.EXISTING_CATEGORY);
        } else {
          crudService.saveCategory(new Category(newCategory, UserLogin.getLoggedUser()));
          View.message("Category \"" + newCategory + "\" created succesfully.");
        }
      }
    }
  }

  public void renameCategory() {
    List<Category> categories = crudService.findAllCategories();
    if (categories.isEmpty()) {
      View.message(Warnings.NO_EXISTING_CATEGORIES);
    } else {
      Object[] categoriesArray = categories.stream().map(Category::getName).toArray();

      String category =
          View.inputOptions("Category selector", "Choose the category to rename", categoriesArray);

      if (category != null) {
        if (!checkCategoryName(category)) {
          View.message("The category " + category + " doesn't exist.");
        } else {
          String newCategory = View.input("Insert a new name for the category: ");
          if (newCategory == null) return;

          if (newCategory.isBlank() || newCategory.isEmpty()) {
            View.message(Warnings.EMPTY_INPUT);
          } else {
            newCategory = newCategory.trim();
            if (newCategory.length() > MAX_CATEGORY_NAME_LENGTH) {
              View.message(LONG_CATEGORY_NAME_WARNING);
            } else if (checkCategoryName(newCategory)) {
              View.message(Warnings.EXISTING_CATEGORY);
            } else if (View.confirm(
                "Are you sure you want to rename \""
                    + category
                    + "\" category to \""
                    + newCategory
                    + "\"?")) {
              Category oldCategory = getCategoryByName(category);
              oldCategory.setName(newCategory);
              crudService.saveCategory(oldCategory);
            }
          }
        }
      }
    }
  }

  public void deleteCategory() {
    List<Category> categories = crudService.findAllCategories();
    if (categories.isEmpty()) {
      View.message(Warnings.NO_EXISTING_CATEGORIES);
    } else {
      Object[] categoriesArray = categories.stream().map(Category::getName).toArray();

      String category =
          View.inputOptions("Category selector", "Choose the category to delete", categoriesArray);

      if (category != null) {
        if (!checkCategoryName(category)) {
          View.message("The category " + category + " doesn't exist.");
        } else if (View.confirm("Are you sure you want to delete \"" + category + "\" category?")) {
          Category categoryToDelete = getCategoryByName(category);
          crudService.deleteCategory(categoryToDelete);
          View.message("Category \"" + categoryToDelete.getName() + "\" deleted succesfully.");
        }
      }
    }
  }

  public void searchCategories() {
    List<Category> categories = crudService.findAllCategories();
    if (categories.isEmpty()) {
      View.message(Warnings.NO_EXISTING_CATEGORIES);
    } else {
      View.displayCategories(categories);
    }
  }

  public boolean checkCategoryName(String category) {
    return crudService.findAllCategories().stream().anyMatch(c -> c.getName().equals(category));
  }

  public Category getCategoryByName(String categoryName) {
    return crudService.findAllCategories().stream()
        .filter(c -> c.getName().equals(categoryName))
        .findFirst()
        .orElseThrow();
  }

  public Optional<Category> askForACategory() {
    Object[] categoriesArray =
        crudService.findAllCategories().stream().map(Category::getName).toArray();

    String category =
        View.inputOptions("Category selector", "Choose the category", categoriesArray);

    if (category == null) {
      return Optional.empty();
    } else {
      Category categoryObj = getCategoryByName(category);
      return Optional.of(categoryObj);
    }
  }
}
