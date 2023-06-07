package controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import javax.swing.*;
import models.*;
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
    operaciones.put("7. Set repeat config", 7);
    operaciones.put("8. Create", 8);
    operaciones.put("9. Cancel", 9);

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
            } else if (name.isBlank() || name.isEmpty()) {
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
          case 4 -> {
            View.display("Description: ");
            String description = scanner.nextLine();

            if (description.length() > 300) {
              View.display("Task description can't be longer than 300 characters!");
            } else if (description.isBlank() || description.isEmpty()) {
              View.display("Not a valid description!");
            } else {
              taskBuilder.setDescription(description);
            }
          }
          case 5 -> {
            View.display("Choose a level of relevance (L = LOW, M = MEDIUM , H = HIGH): ");

            Relevance relevance;

            switch (scanner.nextLine()) {
              case "L" -> relevance = Relevance.LOW;
              case "M" -> relevance = Relevance.MEDIUM;
              case "H" -> relevance = Relevance.HIGH;
              default -> {
                View.display("Not a valid relevance.");
                continue;
              }
            }

            taskBuilder.setRelevance(relevance);
          }
          case 6 -> {
            View.display("Category: ");
            String category = scanner.nextLine();

            if (!crudService.checkCategoryName(category)) {
              View.display("The category " + category + " doesn't exist.");
            } else {
              Category categoryObj = crudService.getCategoryByName(category);
              taskBuilder.setCategory(categoryObj);
            }
          }
          case 7 -> {
            if (taskBuilder.build().getRepeatingConfig() != null) {
              View.display("Do you want to set this task as not repetitive (Y/N): ");
              String confirmation = scanner.nextLine();

              if (confirmation.equalsIgnoreCase("Y")) {
                taskBuilder.setRepeatingConfig(null);
                continue;
              }
            }

            RepeatTaskConfig repeatingConfig = new RepeatTaskConfig();

            View.display(
                "Choose a type of repetition (H = HOUR, D = DAILY , W = WEEKLY, M = MONTHLY, Y = YEARLY): ");

            RepeatType repeatType;

            switch (scanner.nextLine()) {
              case "H" -> repeatType = RepeatType.HOUR;
              case "D" -> repeatType = RepeatType.DAILY;
              case "W" -> repeatType = RepeatType.WEEKLY;
              case "M" -> repeatType = RepeatType.MONTHLY;
              case "Y" -> repeatType = RepeatType.YEARLY;
              default -> {
                View.display("Not a valid type of repetition.");
                continue;
              }
            }

            repeatingConfig.setRepeatType(repeatType);

            View.display("Do you want to set a end date for repetitions (Y/N): ");
            String confirmation = scanner.nextLine();

            if (confirmation.equalsIgnoreCase("Y")) {
              View.display("Repeat ends at (yyyy-mm-dd): ");
              String repeatEndsAtStr = scanner.nextLine();

              try {
                LocalDate repeatEndsAt = LocalDate.parse(repeatEndsAtStr);
                repeatingConfig.setRepeatEndsAt(repeatEndsAt);
              } catch (DateTimeParseException e) {
                View.display("Invalid end date format");
              }
            }

            // Preguntar por interval y luego por repeatOnConfig
          }
          case 8 -> {
            if (taskBuilder.build().getName() != null) {
              View.display(
                  "Are you sure you want to create \""
                      + taskBuilder.build().getName()
                      + "\"? (Y/N): ");
              String confirmation = scanner.nextLine();

              if (confirmation.equalsIgnoreCase("Y")) {
                Task newTask = taskBuilder.build();
                crudService.saveTask(newTask);
              }
            } else {
              View.display("You have to set a name for this task!");
            }
          }
          case 9 -> opcionIndice =
              (JOptionPane.showConfirmDialog(null, "Are you sure you want to cancel?") == 0)
                  ? 10
                  : 0;
        }
      }
    } while (opcionIndice != 10);
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

    if (!crudService.checkCategoryName(category)) {
      View.display("The category " + category + " doesn't exist.");
    } else {
      Category categoryObj = crudService.getCategoryByName(category);

      if (searchService.getCategoryTasks(categoryObj).size() != 0) {
        View.displayTasksByCategory(searchService.getCategoryTasks(categoryObj), categoryObj);
      } else {
        View.display("You don't have task in this category.");
      }
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
