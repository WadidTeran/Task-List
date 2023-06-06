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

    View.display("Insert category name to change: ");
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

  public void searchCategoryTasks() {
    FilteredTaskSearchService searchService = new FilteredTaskSearchService();

    View.display("Insert category name to search: ");
    String category = scanner.nextLine();

    if (crudService.checkCategoryName(category)) {
      Category categoryObj = crudService.getCategoryByName(category);
      View.displayTasksByCategory(searchService.getCategoryTasks(categoryObj), categoryObj);
    } else {
      View.display("The category " + category + " doesn't exist.");
    }
  }

  public void deleteCategory() {}

  public void searchCategories() {
    View.displayCategories(crudService.findAllCategories());
  }
}
