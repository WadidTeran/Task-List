package controllers;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import models.Category;
import services.CRUDServiceImpl;
import utils.UserLogin;
import views.View;

public class CategoryController {
  private final CRUDServiceImpl crudService;

  public CategoryController(CRUDServiceImpl crudService) {
    this.crudService = crudService;
  }

  public void createCategory() {
    if (crudService.findAllCategories().size() >= 10) {
      JOptionPane.showMessageDialog(null, "You cannot create more than 10 categories!");
    } else {
      String newCategory = JOptionPane.showInputDialog("Insert the new category's name: ");

      if (newCategory.length() > 50) {
        JOptionPane.showMessageDialog(null, "Category names cannot be longer than 50 characters!");
      } else if (newCategory.isBlank() || newCategory.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Not a valid name!");
      } else if (crudService.checkCategoryName(newCategory)) {
        JOptionPane.showMessageDialog(null, "This category already exists!");
      } else {
        crudService.saveCategory(new Category(newCategory, UserLogin.getUser()));
        JOptionPane.showMessageDialog(null, "Category created succesfully.");
      }
    }
  }

  public void renameCategory() {
    Object[] categoriesArray =
        crudService.findAllCategories().stream().map(Category::getName).toArray();

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

      if (newCategory.length() > 50) {
        JOptionPane.showMessageDialog(null, "Category names cannot be longer than 50 characters!");
      } else if (crudService.checkCategoryName(newCategory)) {
        JOptionPane.showMessageDialog(null, "This category name is already in use!");
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

  public void deleteCategory() {
    Object[] categoriesArray =
        crudService.findAllCategories().stream().map(Category::getName).toArray();

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
    }
  }

  public void searchCategories() {
    ArrayList<Category> categories = crudService.findAllCategories();
    if (categories.isEmpty()) {
      JOptionPane.showMessageDialog(null, "You don't have any categories created.");
    } else {
      View.displayCategories(categories);
    }
  }
}
