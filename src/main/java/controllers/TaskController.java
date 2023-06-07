package controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import javax.swing.*;
import models.Category;
import models.Relevance;
import models.Task;
import models.TaskBuilder;
import services.CRUDServiceImpl;
import services.FilteredTaskSearchService;
import utils.RepetitiveTaskManager;
import views.View;

public class TaskController {
  private final Scanner scanner = new Scanner(System.in);
  private final CRUDServiceImpl crudService;
  private final FilteredTaskSearchService searchService;

  public TaskController(CRUDServiceImpl crudService, FilteredTaskSearchService searchService) {
    this.crudService = crudService;
    this.searchService = searchService;
  }

  public void createTask() {
    View.display("Set basic information for the task");
    Map<String, Integer> operaciones = new HashMap<>();
    operaciones.put("1. Set name", 1);
    operaciones.put("2. Set due date", 2);
    operaciones.put("3. Set a specified time", 3);
    operaciones.put("4. Set description", 4);
    operaciones.put("5. Set relevance", 5);
    operaciones.put("6. Set category", 6);
    operaciones.put("7. Next", 7);
    operaciones.put("8. Cancel", 8);

    Object[] opArreglo = operaciones.keySet().toArray();

    int opcionIndice = 0;

    TaskBuilder taskBuilder = new TaskBuilder();

    do {
      String opcion =
          (String)
              JOptionPane.showInputDialog(
                  null,
                  "Choose an option",
                  "Task create mode",
                  JOptionPane.INFORMATION_MESSAGE,
                  null,
                  opArreglo,
                  opArreglo[0]);

      if (opcion == null) {
        JOptionPane.showMessageDialog(null, "You have to choose an option!");
      } else {
        opcionIndice = operaciones.get(opcion);

        switch (opcionIndice) {
          case 1 -> {
            View.display("Name: ");
            String name = scanner.nextLine();

            if (name.length() > 50) {
              View.display("Task names can't be longer than 50 characters!");
            } else if (name.isBlank() || name.isEmpty()){
              View.display("Not a valid name!");
            } else {
              taskBuilder.setName(name);
            }
          }
          case 2 -> {
            View.display("Due date (yyyy-mm-dd): ");
            String dueDateStr = scanner.nextLine();

            try {
              LocalDate dueDate = LocalDate.parse(dueDateStr);
              taskBuilder.setDueDate(dueDate);
            } catch (DateTimeParseException e) {
              View.display("Invalid due date format");
            }

          }
          case 3 -> {
            if (taskBuilder.build().getDueDate() != null) {
              View.display("Specified time in 24h time system (hh:mm): ");
              String specifiedTimeStr = scanner.nextLine();

              try {
                LocalTime specifiedTime = LocalTime.parse(specifiedTimeStr);
                taskBuilder.setSpecifiedTime(specifiedTime);
              } catch (DateTimeParseException e) {
                View.display("Invalid time format");
              }
            } else {
              View.display("You have to set a due date first!");
            }
          }
          case 4 -> {}
          case 5 -> {}
          case 6 -> {}
          case 7 -> {
            if (taskBuilder.build().getName() != null) {

            } else {
              View.display("You have to set a name for this task!");
            }


          }
          case 8 -> opcionIndice =
              (JOptionPane.showConfirmDialog(null, "Are you sure you want to cancel?") == 0)
                  ? 9
                  : 0;
        }
      }
    } while (opcionIndice != 9);
  }

  public void modifyTask() {}

  public void setAsCompletedTask() {
    searchAllPendingTasks();
    View.display("Insert the task's id you want to set as completed: ");
    String taskToSet = scanner.nextLine();

    try {
      Optional<Task> optionalTask = crudService.getTaskById(Long.valueOf(taskToSet));
      if (optionalTask.isPresent()) {
        Task task = optionalTask.get();
        View.display(
            "Are you sure you want to set as completed \"" + task.getName() + "\"? (Y/N): ");
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("Y")) {
          task.setCompleted(true);
          task.setCompletedDate(LocalDate.now());
          crudService.saveTask(task);
          if (task.getRepeatingConfig() != null) {
            RepetitiveTaskManager.manageRepetitiveTask(task, crudService);
          }
        }
      } else {
        View.display("The task id doesn't exist.");
      }
    } catch (NumberFormatException e) {
      View.display("That is not a number.");
    }
  }

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
