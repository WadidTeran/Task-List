package controllers;

import java.util.Optional;
import java.util.Scanner;
import models.Category;
import models.Relevance;
import models.Task;
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

  public void setAsPendingTask() {
    searchCompletedTasks();
    View.display("Insert the task's id you want to set as pending: ");
    String taskToSet = scanner.nextLine();

    try {
      Optional<Task> optionalTask = crudService.getTaskById(Long.valueOf(taskToSet));
      if (optionalTask.isPresent()) {
        Task task = optionalTask.get();
        View.display("Are you sure you want to set as pending \"" + task.getName() + "\"? (Y/N): ");
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("Y")) {
          task.setCompleted(false);
          task.setCompletedDate(null);
          crudService.saveTask(task);
        }
      } else {
        View.display("The task id doesn't exist.");
      }
    } catch (NumberFormatException e) {
      View.display("That is not a number.");
    }
  }

  public void modifyTask() {}

  public void deleteTask() {
    searchAllPendingTasks();
    View.display("Insert the task's id you want to delete: ");
    String taskToDelete = scanner.nextLine();

    try {
      Optional<Task> optionalTask = crudService.getTaskById(Long.valueOf(taskToDelete));
      if (optionalTask.isPresent()) {
        Task task = optionalTask.get();
        View.display("Are you sure you want to delete \"" + task.getName() + "\"? (Y/N): ");
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("Y")) {
          crudService.deleteTask(task);
        }
      } else {
        View.display("The task id doesn't exist.");
      }
    } catch (NumberFormatException e) {
      View.display("That is not a number.");
    }
  }

  public void searchFuturePendingTasks() {
    if (searchService.getFuturePendingTasks().size() != 0) {
      View.displayFuturePendingTasks(searchService.getFuturePendingTasks());
    } else {
      View.display("You don't have future pending tasks.");
    }
  }

  public void searchPendingTasksForToday() {
    if (searchService.getPendingTasksForToday().size() != 0) {
      View.displayPendingTasksForToday(searchService.getPendingTasksForToday());
    } else {
      View.display("You don't have tasks for today.");
    }
  }

  public void searchPastPendingTasks() {
    if (searchService.getPastPendingTasks().size() != 0) {
      View.displayPastPendingTasks(searchService.getPastPendingTasks());
    } else {
      View.display("You don't have previous pending tasks.");
    }
  }

  public void searchAllPendingTasks() {
    if (searchService.getAllPendingTasks().size() != 0) {
      View.displayAllPendingTasks(searchService.getAllPendingTasks());
    } else {
      View.display("You don't have pending tasks.");
    }
  }

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
    if (searchService.getRelevanceTasks(relevance).size() != 0) {
      View.displayTasksByRelevance(searchService.getRelevanceTasks(relevance), relevance);
    } else {
      View.display("You don't have task with this relevance.");
    }
  }

  public void searchTasksByCategory() {
    View.display("Insert the name of the category to search: ");
    String category = scanner.nextLine();

    if (crudService.checkCategoryName(category)) {
      Category categoryObj = crudService.getCategoryByName(category);

      if (searchService.getCategoryTasks(categoryObj).size() != 0) {
        View.displayTasksByCategory(searchService.getCategoryTasks(categoryObj), categoryObj);
      } else {
        View.display("You don't have task in this category.");
      }
    } else {
      View.display("The category " + category + " doesn't exist.");
    }
  }

  public void searchCompletedTasks() {
    if (searchService.getCompletedTasks().size() != 0) {
      View.displayCompletedTasks(searchService.getCompletedTasks());
    } else {
      View.display("You don't have completed tasks.");
    }
  }

  public void deleteCompletedTasks() {
    if (searchService.getCompletedTasks().size() != 0) {
      View.display("Are you sure you want to delete all completed tasks? (Y/N): ");
      String confirmation = scanner.nextLine();
      if (confirmation.equalsIgnoreCase("Y")) {
        crudService.deleteCompletedTasks();
      }
    } else {
      View.display("You don't have completed tasks.");
    }
  }
}
