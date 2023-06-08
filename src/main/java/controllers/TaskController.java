package controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.swing.JOptionPane;
import models.*;
import services.CRUDServiceImpl;
import services.FilteredTaskSearchService;
import utils.RepetitiveTaskManager;
import utils.TaskCreatorModificator;
import utils.TaskOperationType;
import views.View;

public class TaskController {
  public static final int MAX_TASK_NAME_LENGTH = 50;
  public static final int MAX_DESCRIPTION_LENGTH = 300;
  public static final String NO_PENDING_TASKS_WARNING = "You don't have any pending tasks.";
  public static final String NO_COMPLETED_TASKS_WARNING = "You don't have any completed tasks.";
  public static final String LONG_TASK_NAME_WARNING =
      "Task names cannot be longer than " + MAX_TASK_NAME_LENGTH + " characters!";
  public static final String MAX_DESCRIPTION_LENGTH_WARNING =
      "Task description can't be longer than " + MAX_DESCRIPTION_LENGTH + " characters!";
  private static final String TASK_NOT_FOUND = "The task id doesn't exist.";
  private static final String NOT_A_NUMBER = "That is not a number.";
  private static final boolean TASK_STATUS_COMPLETED = true;
  private static final boolean TASK_STATUS_PENDING = false;
  private final CRUDServiceImpl crudService;
  private final FilteredTaskSearchService searchService;

  public TaskController(CRUDServiceImpl crudService, FilteredTaskSearchService searchService) {
    this.crudService = crudService;
    this.searchService = searchService;
  }

  public void createTask() {
    TaskCreatorModificator.process(new TaskBuilder(), TaskOperationType.CREATION, crudService);
  }

  public void modifyTask() {
    ArrayList<Task> tasks = searchService.getAllPendingTasks();
    if (tasks.isEmpty()) {
      JOptionPane.showMessageDialog(null, NO_PENDING_TASKS_WARNING);
    } else {
      try {
        Optional<Task> optTask = askForATask("Choose a task to modify", TASK_STATUS_PENDING);
        if (optTask.isPresent()) {
          Task taskToModify = optTask.get();
          TaskBuilder taskBuilder = new TaskBuilder(taskToModify);

          TaskCreatorModificator.process(taskBuilder, TaskOperationType.MODIFICATION, crudService);
        } else {
          JOptionPane.showMessageDialog(null, "The task's id doesn't exist!");
        }
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Not a valid task id.");
      }
    }
  }

  public void setAsCompletedTask() {
    ArrayList<Task> tasks = searchService.getAllPendingTasks();
    if (tasks.isEmpty()) {
      JOptionPane.showMessageDialog(null, NO_PENDING_TASKS_WARNING);
    } else {
      try {
        Optional<Task> optionalTask =
            askForATask("Choose a task to set as completed", TASK_STATUS_PENDING);
        if (optionalTask.isPresent()) {
          Task task = optionalTask.get();

          if (JOptionPane.showConfirmDialog(
                  null, "Are you sure you want to set as completed \"" + task.getName() + "\"?")
              == 0) {
            task.setCompleted(true);
            task.setCompletedDate(LocalDate.now());
            crudService.saveTask(task);
            if (task.getRepeatingConfig() != null) {
              RepetitiveTaskManager.manageRepetitiveTask(task, crudService);
            }
          }
        } else {
          JOptionPane.showMessageDialog(null, TASK_NOT_FOUND);
        }
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, NOT_A_NUMBER);
      }
    }
  }

  public void setAsPendingTask() {
    ArrayList<Task> tasks = searchService.getCompletedTasks();
    if (tasks.isEmpty()) {
      JOptionPane.showMessageDialog(null, NO_COMPLETED_TASKS_WARNING);
    } else {
      try {
        Optional<Task> optionalTask =
            askForATask("Choose a task to set as pending", TASK_STATUS_COMPLETED);
        if (optionalTask.isPresent()) {
          Task task = optionalTask.get();

          if (JOptionPane.showConfirmDialog(
                  null, "Are you sure you want to set as pending \"" + task.getName() + "\"?")
              == 0) {
            task.setCompleted(false);
            task.setCompletedDate(null);
            crudService.saveTask(task);
          }
        } else {
          JOptionPane.showMessageDialog(null, TASK_NOT_FOUND);
        }
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, NOT_A_NUMBER);
      }
    }
  }

  public void deleteTask() {
    ArrayList<Task> tasks = searchService.getAllPendingTasks();
    if (tasks.isEmpty()) {
      JOptionPane.showMessageDialog(null, NO_PENDING_TASKS_WARNING);
    } else {
      try {
        Optional<Task> optionalTask = askForATask("Choose a task to delete", TASK_STATUS_PENDING);
        if (optionalTask.isPresent()) {
          Task task = optionalTask.get();

          if (JOptionPane.showConfirmDialog(
                  null, "Are you sure you want to delete \"" + task.getName() + "\"?")
              == 0) {
            crudService.deleteTask(task);
          }
        } else {
          JOptionPane.showMessageDialog(null, TASK_NOT_FOUND);
        }
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, NOT_A_NUMBER);
      }
    }
  }

  public void searchFuturePendingTasks() {
    ArrayList<Task> tasks = searchService.getAllPendingTasks();
    if (tasks.isEmpty()) {
      JOptionPane.showMessageDialog(null, NO_PENDING_TASKS_WARNING);
    } else {
      ArrayList<Task> futurePendingTasks = searchService.getFuturePendingTasks();
      if (futurePendingTasks.isEmpty()) {
        JOptionPane.showMessageDialog(null, "You don't have future pending tasks.");
      } else {
        View.displayFuturePendingTasks(futurePendingTasks);
      }
    }
  }

  public void searchPendingTasksForToday() {
    ArrayList<Task> tasks = searchService.getAllPendingTasks();
    if (tasks.isEmpty()) {
      JOptionPane.showMessageDialog(null, NO_PENDING_TASKS_WARNING);
    } else {
      ArrayList<Task> pendingTasksForToday = searchService.getPendingTasksForToday();
      if (pendingTasksForToday.isEmpty()) {
        JOptionPane.showMessageDialog(null, "You don't have pending tasks for today.");
      } else {
        View.displayPendingTasksForToday(pendingTasksForToday);
      }
    }
  }

  public void searchPastPendingTasks() {
    ArrayList<Task> tasks = searchService.getAllPendingTasks();
    if (tasks.isEmpty()) {
      JOptionPane.showMessageDialog(null, NO_PENDING_TASKS_WARNING);
    } else {
      ArrayList<Task> pastPendingTasks = searchService.getPastPendingTasks();
      if (pastPendingTasks.isEmpty()) {
        JOptionPane.showMessageDialog(null, "You don't have previous pending tasks.");
      } else {
        View.displayPastPendingTasks(pastPendingTasks);
      }
    }
  }

  public void searchAllPendingTasks() {
    ArrayList<Task> pendingTasks = searchService.getAllPendingTasks();
    if (pendingTasks.isEmpty()) {
      JOptionPane.showMessageDialog(null, NO_PENDING_TASKS_WARNING);
    } else {
      View.displayAllPendingTasks(pendingTasks);
    }
  }

  public void searchTasksByRelevance() {
    Map<String, Relevance> relevanceMap = new HashMap<>();
    relevanceMap.put("None", Relevance.NONE);
    relevanceMap.put("Low", Relevance.LOW);
    relevanceMap.put("Medium", Relevance.MEDIUM);
    relevanceMap.put("High", Relevance.HIGH);

    Object[] relevanceOptionsArray = relevanceMap.keySet().toArray();

    String relevanceStr =
        (String)
            JOptionPane.showInputDialog(
                null,
                "Choose a level of relevance",
                "Tasks by relevance",
                JOptionPane.INFORMATION_MESSAGE,
                null,
                relevanceOptionsArray,
                relevanceOptionsArray[0]);

    if (relevanceStr == null) {
      JOptionPane.showMessageDialog(null, "You have to choose a level of relevance!");
    } else {
      Relevance relevance = relevanceMap.get(relevanceStr);
      if (searchService.getRelevanceTasks(relevance).isEmpty()) {
        JOptionPane.showMessageDialog(null, "You don't have task with this relevance.");
      } else {
        View.displayTasksByRelevance(searchService.getRelevanceTasks(relevance), relevance);
      }
    }
  }

  public void searchTasksByCategory() {
    ArrayList<Category> categories = crudService.findAllCategories();
    if (categories.isEmpty()) {
      JOptionPane.showMessageDialog(null, CategoryController.EMPTY_CATEGORIES_WARNING);
    } else {
      Object[] categoriesArray = categories.stream().map(Category::getName).toArray();

      String category =
          (String)
              JOptionPane.showInputDialog(
                  null,
                  "Choose the category",
                  "Search tasks by category",
                  JOptionPane.INFORMATION_MESSAGE,
                  null,
                  categoriesArray,
                  categoriesArray[0]);

      if (category == null) {
        JOptionPane.showMessageDialog(null, "You have to choose a category!");
      } else if (!crudService.checkCategoryName(category)) {
        JOptionPane.showMessageDialog(null, "The category " + category + " doesn't exist.");
      } else {
        Category categoryObj = crudService.getCategoryByName(category);

        if (searchService.getCategoryTasks(categoryObj).isEmpty()) {
          JOptionPane.showMessageDialog(null, "You don't have task in this category.");
        } else {
          View.displayTasksByCategory(searchService.getCategoryTasks(categoryObj), categoryObj);
        }
      }
    }
  }

  public void searchCompletedTasks() {
    if (searchService.getCompletedTasks().isEmpty()) {
      JOptionPane.showMessageDialog(null, NO_COMPLETED_TASKS_WARNING);
    } else {
      View.displayCompletedTasks(searchService.getCompletedTasks());
    }
  }

  public void deleteCompletedTasks() {
    if (searchService.getCompletedTasks().isEmpty()) {
      JOptionPane.showMessageDialog(null, NO_COMPLETED_TASKS_WARNING);
    } else if (JOptionPane.showConfirmDialog(
            null, "Are you sure you want to delete all completed tasks?")
        == 0) {
      crudService.deleteCompletedTasks(searchService);
    }
  }

  public void searchOneTask() {
    if (searchService.getAllPendingTasks().isEmpty()) {
      JOptionPane.showMessageDialog(null, NO_PENDING_TASKS_WARNING);
    } else {
      try {
        Optional<Task> optTask = askForATask("Choose a task to search", TASK_STATUS_PENDING);
        if (optTask.isPresent()) {
          View.displayOneTask(optTask.get());
        } else {
          JOptionPane.showMessageDialog(null, "The task's id doesn't exist!");
        }
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Not a valid task id.");
      }
    }
  }

  private Optional<Task> askForATask(String message, boolean taskStatus) {
    ArrayList<Task> tasks =
        (taskStatus == TASK_STATUS_COMPLETED)
            ? searchService.getCompletedTasks()
            : searchService.getAllPendingTasks();

    Object[] taskArray = tasks.stream().map(t -> t.getTaskId() + " - " + t.getName()).toArray();

    String task =
        (String)
            JOptionPane.showInputDialog(
                null,
                message,
                "Task selector",
                JOptionPane.INFORMATION_MESSAGE,
                null,
                taskArray,
                taskArray[0]);

    if (task == null) {
      JOptionPane.showMessageDialog(null, "You have to choose a task!");
      return Optional.empty();
    } else {
      return crudService.getTaskById(Long.parseLong(task.split("-")[0].trim()));
    }
  }
}
