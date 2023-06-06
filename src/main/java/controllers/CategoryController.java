package controllers;

import java.util.Scanner;
import models.Category;
import repositories.TaskRepository;
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

  public void renameCategory() {}

  public void searchCategoryTasks() {
    FilteredTaskSearchService searchService = new FilteredTaskSearchService(new TaskRepository());

    View.display("Insert category name: ");
    String category = scanner.nextLine();

    if (crudService.checkCategoryName(category)) {
      Category categoryObj = crudService.getCategoryByName(category);
      View.displayTasksByCategory(searchService.getCategoryTasks(categoryObj), categoryObj);
    } else {
      View.display("The category " + category + " doesn't exist.");
    }
  }

  public void deleteCategory() {}

  public void searchCategories() {}
}
