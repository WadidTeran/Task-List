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

  public void searchFuturePendingTasks() {
    if(searchService.getFuturePendingTasks().size() != 0){
      View.displayFuturePendingTasks(searchService.getFuturePendingTasks());
    } else {
      View.display("You don't have future pending tasks");
    }
  }

  public void searchPendingTasksForToday() {
    if(searchService.getPendingTasksForToday().size() != 0){
      View.displayPendingTasksForToday(searchService.getPendingTasksForToday());
    } else {
      View.display("You don't have task for today");
    }

  }

  public void searchPastPendingTasks() {
    if(searchService.getPastPendingTasks().size() != 0){
      View.displayPastPendingTasks(searchService.getPastPendingTasks());
    } else {
      View.display("You don't have past pending tasks ");
    }
  }

  public void searchAllPendingTasks() {
    if(searchService.getAllPendingTasks().size() != 0){
      View.displayAllPendingTasks(searchService.getPendingTasksForToday());
    } else {
      View.display("You don't have pending tasks");
    }
  }

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
