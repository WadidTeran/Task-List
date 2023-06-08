package controllers;

import java.time.LocalDate;
import java.util.*;
import javax.swing.JOptionPane;
import models.*;
import services.CRUDServiceImpl;
import services.FilteredTaskSearchService;
import utils.RepetitiveTaskManager;
import utils.TaskCreatorModificator;
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
  private final CRUDServiceImpl crudService;
  private final FilteredTaskSearchService searchService;

  public TaskController(CRUDServiceImpl crudService, FilteredTaskSearchService searchService) {
    this.crudService = crudService;
    this.searchService = searchService;
  }

  public void createTask() {
    Map<String, Integer> menuOptions = new HashMap<>();
    menuOptions.put("Set name", 1);
    menuOptions.put("Set due date", 2);
    menuOptions.put("Set a specified time", 3);
    menuOptions.put("Set description", 4);
    menuOptions.put("Set relevance", 5);
    menuOptions.put("Set category", 6);
    menuOptions.put("Set repeat config", 7);
    menuOptions.put("Create", 8);
    menuOptions.put("Cancel", 9);

    TaskCreatorModificator.process(
        new TaskBuilder(), "Task creation mode", menuOptions, crudService);
  }

  public void modifyTask() {
    ArrayList<Task> tasks = searchService.getAllPendingTasks();
    if (tasks.isEmpty()) {
      JOptionPane.showMessageDialog(null, NO_PENDING_TASKS_WARNING);
    } else {
      searchAllPendingTasks();
      try {
        Long taskId = Long.valueOf(JOptionPane.showInputDialog("Insert task's id: "));
        Optional<Task> optTask = crudService.getTaskById(taskId);
        if (optTask.isPresent()) {
          Task taskToModify = optTask.get();
          TaskBuilder taskBuilder = new TaskBuilder(taskToModify);

          Map<String, Integer> menuOptions = new HashMap<>();
          menuOptions.put("Change name", 1);
          menuOptions.put("Change due date", 2);
          menuOptions.put("Change specified time", 3);
          menuOptions.put("Change description", 4);
          menuOptions.put("Change relevance", 5);
          menuOptions.put("Change category", 6);
          menuOptions.put("Change repeat config", 7);
          menuOptions.put("Confirm changes", 8);
          menuOptions.put("Cancel", 9);

          TaskCreatorModificator.process(
              taskBuilder, "Task modification mode", menuOptions, crudService);
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
      searchAllPendingTasks();
      String taskToSet =
          JOptionPane.showInputDialog("Insert the task's id you want to set as completed: ");
      try {
        Optional<Task> optionalTask = crudService.getTaskById(Long.valueOf(taskToSet));
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
          JOptionPane.showMessageDialog(null, "The task id doesn't exist.");
        }
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "That is not a number.");
      }
    }
  }

  public void setAsPendingTask() {
    ArrayList<Task> tasks = searchService.getCompletedTasks();
    if (tasks.isEmpty()) {
      JOptionPane.showMessageDialog(null, NO_COMPLETED_TASKS_WARNING);
    } else {
      searchCompletedTasks();
      String taskToSet =
          JOptionPane.showInputDialog("Insert the task's id you want to set as pending: ");

      try {
        Optional<Task> optionalTask = crudService.getTaskById(Long.valueOf(taskToSet));
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
          JOptionPane.showMessageDialog(null, "The task id doesn't exist.");
        }
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "That is not a number.");
      }
    }
  }

  public void deleteTask() {
    ArrayList<Task> tasks = searchService.getAllPendingTasks();
    if (tasks.isEmpty()) {
      JOptionPane.showMessageDialog(null, NO_PENDING_TASKS_WARNING);
    } else {
      searchAllPendingTasks();

      String taskToDelete =
          JOptionPane.showInputDialog("Insert the task's id you want to delete: ");

      try {
        Optional<Task> optionalTask = crudService.getTaskById(Long.valueOf(taskToDelete));
        if (optionalTask.isPresent()) {
          Task task = optionalTask.get();

          if (JOptionPane.showConfirmDialog(
                  null, "Are you sure you want to delete \"" + task.getName() + "\"?")
              == 0) {
            crudService.deleteTask(task);
          }
        } else {
          JOptionPane.showMessageDialog(null, "The task id doesn't exist.");
        }
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "That is not a number.");
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
      searchAllPendingTasks();
      try {
        Long taskId = Long.valueOf(JOptionPane.showInputDialog("Insert task's id: "));
        Optional<Task> optTask = crudService.getTaskById(taskId);
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
}
