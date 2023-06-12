package services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;

import models.*;
import views.View;

public class TaskService {
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
  private final ICRUDService crudService;
  private final CategoryService categoryService;
  private final Map<String, RepeatType> repeatTypeMap;
  private final Object[] repeatTypeArray;
  private final Map<String, Relevance> relevanceMap;
  private final Object[] relevanceArray;

  public TaskService(ICRUDService crudService, CategoryService categoryService) {
    this.crudService = crudService;
    this.categoryService = categoryService;

    repeatTypeMap = new LinkedHashMap<>();
    repeatTypeMap.put("Hour", RepeatType.HOUR);
    repeatTypeMap.put("Daily", RepeatType.DAILY);
    repeatTypeMap.put("Weekly", RepeatType.WEEKLY);
    repeatTypeMap.put("Monthly", RepeatType.MONTHLY);
    repeatTypeMap.put("Yearly", RepeatType.YEARLY);
    repeatTypeArray = repeatTypeMap.keySet().toArray();

    relevanceMap = new LinkedHashMap<>();
    relevanceMap.put("NONE", Relevance.NONE);
    relevanceMap.put("LOW", Relevance.LOW);
    relevanceMap.put("MEDIUM", Relevance.MEDIUM);
    relevanceMap.put("HIGH", Relevance.HIGH);
    relevanceArray = relevanceMap.keySet().toArray();
  }

  public void createTask() {
    TaskCreatorModificatorService.process(
        new TaskBuilder(), TaskOperationType.CREATION, crudService, categoryService);
  }

  public void modifyTask() {
    if (checkExistenceOfTasks(TASK_STATUS_PENDING)) {
      try {
        Optional<Task> optTask = askForATask("Choose a task to modify", TASK_STATUS_PENDING);
        if (optTask.isPresent()) {
          Task taskToModify = optTask.get();
          TaskBuilder taskBuilder = new TaskBuilder(taskToModify);

          TaskCreatorModificatorService.process(
              taskBuilder, TaskOperationType.MODIFICATION, crudService, categoryService);
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
    if (checkExistenceOfTasks(TASK_STATUS_PENDING)) {
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
            if (task.isRepetitive()) {
              manageRepetitiveTask(task);
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
    if (checkExistenceOfTasks(TASK_STATUS_COMPLETED)) {
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
    if (checkExistenceOfTasks(TASK_STATUS_PENDING)) {
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
    List<Task> futurePendingTasks =
        getAllPendingTasks().stream()
            .filter(
                t -> {
                  if (t.getDueDate() != null) {
                    return t.getDueDate().isAfter(LocalDate.now());
                  }
                  return false;
                })
            .collect(Collectors.toList());
    if (futurePendingTasks.isEmpty()) {
      View.message("You don't have future pending tasks.");
    } else {
      View.displayFuturePendingTasks(futurePendingTasks);
    }
  }

  public void searchPendingTasksForToday() {
    List<Task> pendingTasksForToday =
        getAllPendingTasks().stream()
            .filter(
                t -> {
                  if (t.getDueDate() != null) {
                    return t.getDueDate().isEqual(LocalDate.now());
                  }
                  return false;
                })
            .collect(Collectors.toList());
    if (pendingTasksForToday.isEmpty()) {
      View.message("You don't have pending tasks for today.");
    } else {
      View.displayPendingTasksForToday(pendingTasksForToday);
    }
  }

  public void searchPastPendingTasks() {
    List<Task> pastPendingTasks =
        getAllPendingTasks().stream()
            .filter(
                t -> {
                  if (t.getDueDate() != null) {
                    return t.getDueDate().isBefore(LocalDate.now());
                  }
                  return false;
                })
            .collect(Collectors.toList());
    if (pastPendingTasks.isEmpty()) {
      View.message("You don't have previous pending tasks.");
    } else {
      View.displayPastPendingTasks(pastPendingTasks);
    }
  }

  public void searchAllPendingTasks() {
    List<Task> pendingTasks = getAllPendingTasks();
    if (pendingTasks.isEmpty()) {
      View.message(NO_PENDING_TASKS_WARNING);
    } else {
      View.displayAllPendingTasks(pendingTasks);
    }
  }

  public void searchTasksByRelevance() {
    if (!checkExistenceOfTasks(TASK_STATUS_PENDING)) {
      View.message(NO_PENDING_TASKS_WARNING);
    } else {
      Optional<Relevance> relevanceOptional = askForARelevance();
      if (relevanceOptional.isPresent()) {
        Relevance relevance = relevanceOptional.get();
        List<Task> relevanceTasks =
            getAllPendingTasks().stream()
                .filter(t -> t.getRelevance() == relevance)
                .collect(Collectors.toList());
        if (relevanceTasks.isEmpty()) {
          View.message("You don't have any task with this relevance.");
        } else {
          View.displayTasksByRelevance(relevanceTasks, relevance);
        }
      }
    }
  }

  public void searchTasksByCategory() {
    if (!checkExistenceOfTasks(TASK_STATUS_PENDING)) {
      View.message(NO_PENDING_TASKS_WARNING);
    } else if (crudService.findAllCategories().isEmpty()) {
      View.message(CategoryService.NO_CATEGORIES_WARNING);
    } else {
      Optional<Category> categoryOptional = categoryService.askForACategory();
      if (categoryOptional.isPresent()) {
        Category categoryObj = categoryOptional.get();
        List<Task> categoryTasks =
            getAllPendingTasks().stream()
                .filter(
                    t -> {
                      if (t.getCategory() != null) {
                        return t.getCategory().getName().equals(categoryObj.getName());
                      }
                      return false;
                    })
                .collect(Collectors.toList());
        if (categoryTasks.isEmpty()) {
          View.message("You don't have tasks in this category.");
        } else {
          View.displayTasksByCategory(categoryTasks, categoryObj);
        }
      }
    }
  }

  public void searchCompletedTasks() {
    List<Task> completedTasks = getCompletedTasks();
    if (completedTasks.isEmpty()) {
      View.message(NO_COMPLETED_TASKS_WARNING);
    } else {
      View.displayCompletedTasks(completedTasks);
    }
  }

  public void deleteCompletedTasks() {
    List<Task> completedTasks = getCompletedTasks();
    if (completedTasks.isEmpty()) {
      View.message(NO_COMPLETED_TASKS_WARNING);
    } else if (View.confirm("Are you sure you want to delete all completed tasks?")) {
      for (Task completedTask : completedTasks) {
        crudService.deleteTask(completedTask);
      }
    }
  }

  public void searchOneTask() {
    if (checkExistenceOfTasks(TASK_STATUS_PENDING)) {
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

  public List<Task> getCompletedTasks() {
    return crudService.findAllTasks().stream()
        .filter(Task::isCompleted)
        .collect(Collectors.toList());
  }

  private Optional<Task> askForATask(String message, boolean taskStatus) {
    List<Task> tasks =
        (taskStatus == TASK_STATUS_COMPLETED) ? getCompletedTasks() : getAllPendingTasks();

    Object[] taskArray = tasks.stream().map(t -> t.getTaskId() + " - " + t.getName()).toArray();

    String task = View.inputOptions("Task selector", message, taskArray);

    if (task == null) {
      return Optional.empty();
    } else {
      String idSection = task.split("-")[0].trim();
      Long id = Long.parseLong(idSection);
      return crudService.getTaskById(id);
    }
  }

  private boolean checkExistenceOfTasks(boolean taskStatus) {
    List<Task> tasks =
        (taskStatus == TASK_STATUS_COMPLETED) ? getCompletedTasks() : getAllPendingTasks();
    return !tasks.isEmpty();
  }

  private List<Task> getAllPendingTasks() {
    return crudService.findAllTasks().stream()
        .filter(t -> !t.isCompleted())
        .collect(Collectors.toList());
  }

  private void manageRepetitiveTask(Task task) {
    NextDueDateCalculatorService nextDueDateCalculatorService =
        new NextDueDateCalculatorService(task);
    LocalDate nextDueDate = nextDueDateCalculatorService.getNextDueDate();

    if (task.getRepeatingConfig().isEndlesslyRepeatable()
        || nextDueDate.isBefore(task.getRepeatingConfig().getRepeatEndsAt())) {
      LocalTime nextSpecifiedTime = nextDueDateCalculatorService.getNextSpecifiedTime();

      Task repeatedTask =
          TaskBuilder.taskBuilderWithClonedTask(task)
              .setDueDate(nextDueDate)
              .setSpecifiedTime(nextSpecifiedTime)
              .build();

      crudService.saveTask(repeatedTask);
    }
  }

  private Optional<Relevance> askForARelevance() {
    String relevance =
        View.inputOptions("Relevance selector", "Choose the relevance", relevanceArray);

    if (relevance == null) {
      return Optional.empty();
    } else {
      return Optional.of(relevanceMap.get(relevance));
    }
  }

  private Optional<RepeatType> askForARepeatType() {
    String repeatType =
        View.inputOptions("Repeat Type selector", "Choose the repeat type", repeatTypeArray);

    if (repeatType == null) {
      return Optional.empty();
    } else {
      return Optional.of(repeatTypeMap.get(repeatType));
    }
  }
}
