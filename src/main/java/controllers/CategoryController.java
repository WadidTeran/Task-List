package controllers;

import java.util.Scanner;
import models.Category;
import services.CRUDServiceImpl;
import services.FilteredTaskSearchService;
import utils.UserLogin;
import views.View;

public class CategoryController {

  private final Scanner scanner = new Scanner(System.in);
  private final CRUDServiceImpl crudService;

  public CategoryController(CRUDServiceImpl crudService) {
    this.crudService = crudService;
  }

  public void createCategory() {
    View.display("Insert the new category's name: ");
    String newCategory = scanner.nextLine();

    if (newCategory.length() > 50) {
      View.display("Category names cannot be longer than 50 characters!");
    } else if (crudService.findAllCategories().size() >= 10) {
      View.display("You cannot create more than 10 categories!");
    } else if (crudService.checkCategoryName(newCategory)) {
      View.display("This category already exists!");
    } else {
      crudService.saveCategory(new Category(newCategory, UserLogin.getUser()));
      View.display("Category created succesfully");
    }
  }

  public void renameCategory() {

    View.display("Insert the name of the category to change: ");
    String category = scanner.nextLine();

    if (!crudService.checkCategoryName(category)) {
      View.display("The category " + category + " doesn't exist.");
    } else {
      View.display("Insert a new name for the category: ");
      String newCategory = scanner.nextLine();

      if (newCategory.length() > 50) {
        View.display("Category names cannot be longer than 50 characters!");
      } else if (crudService.checkCategoryName(newCategory)) {
        View.display("This category name is already in use!");
      } else {
        Category oldCategory = crudService.getCategoryByName(category);
        oldCategory.setName(newCategory);
        crudService.saveCategory(oldCategory);
      }
    }
  }

  public void deleteCategory() {
    View.display("Insert the name of the category to delete: ");
    String category = scanner.nextLine();

    if (!crudService.checkCategoryName(category)) {
      View.display("The category " + category + " doesn't exist.");
    } else {
      Category categoryToDelete = crudService.getCategoryByName(category);
      crudService.deleteCategory(categoryToDelete);
    }
  }

  public void searchCategories() {
    View.displayCategories(crudService.findAllCategories());
  }
}
