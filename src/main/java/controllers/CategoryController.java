package controllers;

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
    String newCategory = JOptionPane.showInputDialog("Insert the new category's name: ");

    if (newCategory.length() > 50) {
      JOptionPane.showMessageDialog(null, "Category names cannot be longer than 50 characters!");
    } else if (newCategory.isBlank() || newCategory.isEmpty()) {
      JOptionPane.showMessageDialog(null, "Not a valid name!");
    } else if (crudService.findAllCategories().size() >= 10) {
      JOptionPane.showMessageDialog(null, "You cannot create more than 10 categories!");
    } else if (crudService.checkCategoryName(newCategory)) {
      JOptionPane.showMessageDialog(null, "This category already exists!");
    } else {
      crudService.saveCategory(new Category(newCategory, UserLogin.getUser()));
      JOptionPane.showMessageDialog(null, "Category created succesfully.");
    }
  }

  public void renameCategory() {
    String category = JOptionPane.showInputDialog("Insert the name of the category to change: ");

    if (!crudService.checkCategoryName(category)) {
      JOptionPane.showMessageDialog(null, "The category " + category + " doesn't exist.");
    } else {
      String newCategory = JOptionPane.showInputDialog("Insert a new name for the category: ");

      if (newCategory.length() > 50) {
        JOptionPane.showMessageDialog(null, "Category names cannot be longer than 50 characters!");
      } else if (crudService.checkCategoryName(newCategory)) {
        JOptionPane.showMessageDialog(null, "This category name is already in use!");
      } else {
        Category oldCategory = crudService.getCategoryByName(category);
        oldCategory.setName(newCategory);
        crudService.saveCategory(oldCategory);
      }
    }
  }

  public void deleteCategory() {
    String category = JOptionPane.showInputDialog("Insert the name of the category to delete: ");

    if (!crudService.checkCategoryName(category)) {
      JOptionPane.showMessageDialog(null, "The category " + category + " doesn't exist.");
    } else {
      Category categoryToDelete = crudService.getCategoryByName(category);
      crudService.deleteCategory(categoryToDelete);
    }
  }

  public void searchCategories() {
    View.displayCategories(crudService.findAllCategories());
  }
}
