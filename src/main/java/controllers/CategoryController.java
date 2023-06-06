package controllers;

import java.util.Scanner;
import models.Category;
import services.CRUDServiceImpl;
import utils.UserLogin;
import views.View;

public class CategoryController {

  private final Scanner scanner = new Scanner(System.in);

  public void createCategory() {
    CRUDServiceImpl crudService = new CRUDServiceImpl();

    View.display("Insert the new category's name: ");
    String newCategory = scanner.nextLine();
    if (newCategory.length() <= 50) {
      if (crudService.findAllCategories().size() < 10) {
        if (crudService.checkCategoryName(newCategory)) {
          crudService.saveCategory(new Category(newCategory, UserLogin.getUser()));
          View.display("Category created succesfully");
        } else {
          View.display("This category already exists!");
        }
      } else {
        View.display("You cannot create more than 10 categories!");
      }

    } else {
      View.display("Category names cannot be longer than 50 characters!");
    }
  }

  public void renameCategory() {}

  public void searchCategoryTasks() {}

  public void deleteCategory() {}

  public void searchCategories() {}
}
