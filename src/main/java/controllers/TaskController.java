package controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
    if (checkExistanceOfTasks(TASK_STATUS_PENDING)) {
      try {
        Optional<Task> optTask = askForATask("Choose a task to modify", TASK_STATUS_PENDING);
        if (optTask.isPresent()) {
          Task taskToModify = optTask.get();
          TaskBuilder taskBuilder = new TaskBuilder(taskToModify);

          TaskCreatorModificator.process(taskBuilder, TaskOperationType.MODIFICATION, crudService);
        } else {
          View.message("The task's id doesn't exist!");
        }
      } catch (NumberFormatException e) {
        View.message("Not a valid task id.");
      }
    } else {
      View.message(NO_PENDING_TASKS_WARNING);
    }
  }

  public void setAsCompletedTask() {
    if (checkExistanceOfTasks(TASK_STATUS_PENDING)) {
      try {
        Optional<Task> optionalTask =
            askForATask("Choose a task to set as completed", TASK_STATUS_PENDING);
        if (optionalTask.isPresent()) {
          Task task = optionalTask.get();

          if (View.confirm(
              "Are you sure you want to set as completed \"" + task.getName() + "\"?")) {
            task.setCompleted(true);
            task.setCompletedDate(LocalDate.now());
            crudService.saveTask(task);
            if (task.getRepeatingConfig() != null) {
              RepetitiveTaskManager.manageRepetitiveTask(task, crudService);
            }
          }
        } else {
          View.message(TASK_NOT_FOUND);
        }
      } catch (NumberFormatException e) {
        View.message(NOT_A_NUMBER);
      }
    } else {
      View.message(NO_PENDING_TASKS_WARNING);
    }
  }

  public void setAsPendingTask() {
    if (checkExistanceOfTasks(TASK_STATUS_COMPLETED)) {
      try {
        Optional<Task> optionalTask =
            askForATask("Choose a task to set as pending", TASK_STATUS_COMPLETED);
        if (optionalTask.isPresent()) {
          Task task = optionalTask.get();

          if (View.confirm("Are you sure you want to set as pending \"" + task.getName() + "\"?")) {
            task.setCompleted(false);
            task.setCompletedDate(null);
            crudService.saveTask(task);
          }
        } else {
          View.message(TASK_NOT_FOUND);
        }
      } catch (NumberFormatException e) {
        View.message(NOT_A_NUMBER);
      }
    } else {
      View.message(NO_COMPLETED_TASKS_WARNING);
    }
  }

  public void deleteTask() {
    if (checkExistanceOfTasks(TASK_STATUS_PENDING)) {
      try {
        Optional<Task> optionalTask = askForATask("Choose a task to delete", TASK_STATUS_PENDING);
        if (optionalTask.isPresent()) {
          Task task = optionalTask.get();

          if (View.confirm("Are you sure you want to delete \"" + task.getName() + "\"?")) {
            crudService.deleteTask(task);
          }
        } else {
          View.message(TASK_NOT_FOUND);
        }
      } catch (NumberFormatException e) {
        View.message(NOT_A_NUMBER);
      }
    } else {
      View.message(NO_PENDING_TASKS_WARNING);
    }
  }

  public void searchFuturePendingTasks() {
    ArrayList<Task> futurePendingTasks = searchService.getFuturePendingTasks();
    if (futurePendingTasks.isEmpty()) {
      View.message("You don't have future pending tasks.");
    } else {
      View.displayFuturePendingTasks(futurePendingTasks);
    }
  }

  public void searchPendingTasksForToday() {
    ArrayList<Task> pendingTasksForToday = searchService.getPendingTasksForToday();
    if (pendingTasksForToday.isEmpty()) {
      View.message("You don't have pending tasks for today.");
    } else {
      View.displayPendingTasksForToday(pendingTasksForToday);
    }
  }

  public void searchPastPendingTasks() {
    ArrayList<Task> pastPendingTasks = searchService.getPastPendingTasks();
    if (pastPendingTasks.isEmpty()) {
      View.message("You don't have previous pending tasks.");
    } else {
      View.displayPastPendingTasks(pastPendingTasks);
    }
  }

  public void searchAllPendingTasks() {
    ArrayList<Task> pendingTasks = searchService.getAllPendingTasks();
    if (pendingTasks.isEmpty()) {
      View.message(NO_PENDING_TASKS_WARNING);
    } else {
      View.displayAllPendingTasks(pendingTasks);
    }
  }

  public void searchTasksByRelevance() {
    if (checkExistanceOfTasks(TASK_STATUS_PENDING)) {
      Map<String, Relevance> relevanceMap = new HashMap<>();
      relevanceMap.put("None", Relevance.NONE);
      relevanceMap.put("Low", Relevance.LOW);
      relevanceMap.put("Medium", Relevance.MEDIUM);
      relevanceMap.put("High", Relevance.HIGH);

      Object[] relevanceOptionsArray = relevanceMap.keySet().toArray();

      String relevanceStr =
          View.inputOptions(
              "Relevance selector", "Choose a level of relevance", relevanceOptionsArray);

      if (relevanceStr == null) {
        View.message("You have to choose a level of relevance!");
      } else {
        Relevance relevance = relevanceMap.get(relevanceStr);
        ArrayList<Task> relevanceTasks = searchService.getRelevanceTasks(relevance);
        if (relevanceTasks.isEmpty()) {
          View.message("You don't have task with this relevance.");
        } else {
          View.displayTasksByRelevance(relevanceTasks, relevance);
        }
      }
    } else {
      View.message(NO_PENDING_TASKS_WARNING);
    }
  }

  public void searchTasksByCategory() {
    if (checkExistanceOfTasks(TASK_STATUS_PENDING)) {
      ArrayList<Category> categories = crudService.findAllCategories();
      if (categories.isEmpty()) {
        View.message(CategoryController.NO_CATEGORIES_WARNING);
      } else {
        Object[] categoriesArray = categories.stream().map(Category::getName).toArray();

        String category =
            View.inputOptions("Category selector", "Choose the category", categoriesArray);

        if (category == null) {
          View.message("You have to choose a category!");
        } else if (!crudService.checkCategoryName(category)) {
          View.message("The category " + category + " doesn't exist.");
        } else {
          Category categoryObj = crudService.getCategoryByName(category);
          ArrayList<Task> categoryTasks = searchService.getCategoryTasks(categoryObj);
          if (categoryTasks.isEmpty()) {
            View.message("You don't have task in this category.");
          } else {
            View.displayTasksByCategory(categoryTasks, categoryObj);
          }
        }
      }
    } else {
      View.message(NO_PENDING_TASKS_WARNING);
    }
  }

  public void searchCompletedTasks() {
    ArrayList<Task> completedTasks = searchService.getCompletedTasks();
    if (completedTasks.isEmpty()) {
      View.message(NO_COMPLETED_TASKS_WARNING);
    } else {
      View.displayCompletedTasks(completedTasks);
    }
  }

  public void deleteCompletedTasks() {
    if (searchService.getCompletedTasks().isEmpty()) {
      View.message(NO_COMPLETED_TASKS_WARNING);
    } else if (View.confirm("Are you sure you want to delete all completed tasks?")) {
      crudService.deleteCompletedTasks(searchService);
    }
  }

  public void searchOneTask() {
    if (checkExistanceOfTasks(TASK_STATUS_PENDING)) {
      try {
        Optional<Task> optTask = askForATask("Choose a task to search", TASK_STATUS_PENDING);
        if (optTask.isPresent()) {
          View.displayOneTask(optTask.get());
        } else {
          View.message("The task's id doesn't exist!");
        }
      } catch (NumberFormatException e) {
        View.message("Not a valid task id.");
      }
    } else {
      View.message(NO_PENDING_TASKS_WARNING);
    }
  }

  private Optional<Task> askForATask(String message, boolean taskStatus) {
    ArrayList<Task> tasks =
        (taskStatus == TASK_STATUS_COMPLETED)
            ? searchService.getCompletedTasks()
            : searchService.getAllPendingTasks();

    Object[] taskArray = tasks.stream().map(t -> t.getTaskId() + " - " + t.getName()).toArray();

    String task = View.inputOptions("Task selector", message, taskArray);

    if (task == null) {
      View.message("You have to choose a task!");
      return Optional.empty();
    } else {
      String idSection = task.split("-")[0].trim();
      Long id = Long.parseLong(idSection);
      return crudService.getTaskById(id);
    }
  }

  private boolean checkExistanceOfTasks(boolean taskStatus) {
    ArrayList<Task> tasks =
        (taskStatus == TASK_STATUS_COMPLETED)
            ? searchService.getCompletedTasks()
            : searchService.getAllPendingTasks();
    return !tasks.isEmpty();
  }
}
