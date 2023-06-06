package controllers;

import java.util.Scanner;
import models.Category;
import models.Relevance;
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

  public void searchTasksByRelevance() {
    View.display("Choose a level of relevance (N = NONE, L = LOW, M = MEDIUM , H = HIGH): ");

    Relevance relevance;

    switch (scanner.nextLine()) {
      case "N" -> relevance = Relevance.NONE;
      case "L" -> relevance = Relevance.LOW;
      case "M" -> relevance = Relevance.MEDIUM;
      case "H" -> relevance = Relevance.HIGH;
      default -> {
        View.display("Not a valid relevance.");
        return;
      }
    }
    View.displayTasksByRelevance(searchService.getRelevanceTasks(relevance), relevance);
  }

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

  public void searchCompletedTasks() {
    View.displayCompletedTasks(searchService.getCompletedTasks());
  }

  public void deleteCompletedTasks() {
    View.display("Are you sure you want to delete all completed tasks? (Y/N): ");
    String confirmation = scanner.nextLine();
    if (confirmation.equalsIgnoreCase("Y")){
      crudService.deleteCompletedTasks();
    }
  }
}
