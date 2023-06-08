package controllers;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import models.Category;
import services.CRUDServiceImpl;
import utils.UserLogin;
import views.View;

public class CategoryController {
  public static final String EMPTY_CATEGORIES_WARNING =
      "You don't have any categories created yet.";
  private static final int MAX_CATEGORY_NAME_LENGTH = 50;
  private static final int MAXIMUM_CATEGORIES = 10;
  private static final String EXISTING_CATEGORY_WARNING = "This category already exists!";
  private static final String LONG_CATEGORY_NAME_WARNING =
      "Category names cannot be longer than " + MAX_CATEGORY_NAME_LENGTH + " characters!";
  private static final String MAXIMUM_CATEGORIES_WARNING =
      "You cannot create more than " + MAXIMUM_CATEGORIES + " categories!";
  private final CRUDServiceImpl crudService;

  public CategoryController(CRUDServiceImpl crudService) {
    this.crudService = crudService;
  }

  public void createCategory() {
    if (crudService.findAllCategories().size() >= MAXIMUM_CATEGORIES) {
      JOptionPane.showMessageDialog(null, MAXIMUM_CATEGORIES_WARNING);
    } else {
      String newCategory = JOptionPane.showInputDialog("Insert the new category's name: ");

      if (newCategory.length() > MAX_CATEGORY_NAME_LENGTH) {
        JOptionPane.showMessageDialog(null, LONG_CATEGORY_NAME_WARNING);
      } else if (newCategory.isBlank() || newCategory.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Not a valid name!");
      } else if (crudService.checkCategoryName(newCategory)) {
        JOptionPane.showMessageDialog(null, EXISTING_CATEGORY_WARNING);
      } else {
        crudService.saveCategory(new Category(newCategory, UserLogin.getUser()));
        JOptionPane.showMessageDialog(
            null, "Category \"" + newCategory + "\" created succesfully.");
      }
    }
  }

  public void renameCategory() {
    ArrayList<Category> categories = crudService.findAllCategories();
    if (categories.isEmpty()) {
      JOptionPane.showMessageDialog(null, EMPTY_CATEGORIES_WARNING);
    } else {
      Object[] categoriesArray = categories.stream().map(Category::getName).toArray();

      String category =
          (String)
              JOptionPane.showInputDialog(
                  null,
                  "Choose the category to rename",
                  "Rename category",
                  JOptionPane.INFORMATION_MESSAGE,
                  null,
                  categoriesArray,
                  categoriesArray[0]);

      if (category == null) {
        JOptionPane.showMessageDialog(null, "You have to choose a category!");
      } else if (!crudService.checkCategoryName(category)) {
        JOptionPane.showMessageDialog(null, "The category " + category + " doesn't exist.");
      } else {
        String newCategory = JOptionPane.showInputDialog("Insert a new name for the category: ");

        if (newCategory.length() > MAX_CATEGORY_NAME_LENGTH) {
          JOptionPane.showMessageDialog(null, LONG_CATEGORY_NAME_WARNING);
        } else if (crudService.checkCategoryName(newCategory)) {
          JOptionPane.showMessageDialog(null, EXISTING_CATEGORY_WARNING);
        } else if (JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to rename \""
                    + category
                    + "\" category to \""
                    + newCategory
                    + "\"?")
            == 0) {
          Category oldCategory = crudService.getCategoryByName(category);
          oldCategory.setName(newCategory);
          crudService.saveCategory(oldCategory);
        }
      }
    }
  }

  public void deleteCategory() {
    ArrayList<Category> categories = crudService.findAllCategories();
    if (categories.isEmpty()) {
      JOptionPane.showMessageDialog(null, EMPTY_CATEGORIES_WARNING);
    } else {
      Object[] categoriesArray = categories.stream().map(Category::getName).toArray();

      String category =
          (String)
              JOptionPane.showInputDialog(
                  null,
                  "Choose the category to delete",
                  "Delete category",
                  JOptionPane.INFORMATION_MESSAGE,
                  null,
                  categoriesArray,
                  categoriesArray[0]);

      if (category == null) {
        JOptionPane.showMessageDialog(null, "You have to choose a category!");
      } else if (!crudService.checkCategoryName(category)) {
        JOptionPane.showMessageDialog(null, "The category " + category + " doesn't exist.");
      } else if (JOptionPane.showConfirmDialog(
              null, "Are you sure you want to delete \"" + category + "\" category?")
          == 0) {
        Category categoryToDelete = crudService.getCategoryByName(category);
        crudService.deleteCategory(categoryToDelete);
        JOptionPane.showMessageDialog(
            null, "Category \"" + categoryToDelete + "\" deleted succesfully.");
      }
    }
  }

  public void searchCategories() {
    ArrayList<Category> categories = crudService.findAllCategories();
    if (categories.isEmpty()) {
      JOptionPane.showMessageDialog(null, EMPTY_CATEGORIES_WARNING);
    } else {
      View.displayCategories(categories);
    }
  }
}
