package controllers;

import java.util.Scanner;
import models.Category;
import services.CRUDServiceImpl;
import services.FilteredTaskSearchService;
import views.View;

public class TaskController {
  private final Scanner scanner = new Scanner(System.in);
  private final CRUDServiceImpl crudService;
  private final FilteredTaskSearchService searchService;

  public TaskController(CRUDServiceImpl crudService, FilteredTaskSearchService searchService) {
    this.crudService = crudService;
    this.searchService = searchService;
  }

  public void createTask() {}

  public void setAsCompletedTask() {}

  public void setAsPendingTask() {}

  public void modifyTask() {}

  public void deleteTask() {}

  public void searchFuturePendingTasks() {}

  public void searchPendingTasksForToday() {}

  public void searchPastPendingTasks() {}

  public void searchAllPendingTasks() {}

  public void searchTasksByRelevance() {}

  public void searchTasksByCategory() {
    View.display("Insert the name of the category to search: ");
    String category = scanner.nextLine();

    if (crudService.checkCategoryName(category)) {
      Category categoryObj = crudService.getCategoryByName(category);
      View.displayTasksByCategory(searchService.getCategoryTasks(categoryObj), categoryObj);
    } else {
      View.display("The category " + category + " doesn't exist.");
    }
  }

  public void searchCompletedTasks() {}

  public void deleteCompletedTasks() {}
}
