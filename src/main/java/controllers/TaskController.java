package controllers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import javax.swing.JOptionPane;
import models.*;
import services.CRUDServiceImpl;
import services.FilteredTaskSearchService;
import utils.RepetitiveTaskManager;
import views.View;

public class TaskController {
  private static final int MAX_TASK_NAME_LENGTH = 50;
  private static final int MAX_DESCRIPTION_LENGTH = 300;
  private static final String NO_PENDING_TASKS_WARNING = "You don't have any pending tasks.";
  private static final String NO_COMPLETED_TASKS_WARNING = "You don't have any completed tasks.";
  private static final String LONG_TASK_NAME_WARNING =
      "Task names cannot be longer than " + MAX_TASK_NAME_LENGTH + " characters!";
  private static final String MAX_DESCRIPTION_LENGTH_WARNING =
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

    taskCreationOrModificationMode(new TaskBuilder(), "Task creation mode", menuOptions);
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

          taskCreationOrModificationMode(taskBuilder, "Task modification mode", menuOptions);
        } else {
          JOptionPane.showMessageDialog(null, "The task's id doesn't exist!");
        }
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Not a valid task id.");
      }
    }
  }

  private void taskCreationOrModificationMode(
      TaskBuilder taskBuilder, String title, Map<String, Integer> menuOptions) {
    Object[] opArreglo = menuOptions.keySet().toArray();
    int opcionIndice = 0;

    do {
      String opcion =
          (String)
              JOptionPane.showInputDialog(
                  null,
                  "Choose an option",
                  title,
                  JOptionPane.INFORMATION_MESSAGE,
                  null,
                  opArreglo,
                  opArreglo[0]);

      if (opcion == null) {
        JOptionPane.showMessageDialog(null, "You have to choose an option!");
      } else {
        opcionIndice = menuOptions.get(opcion);

        switch (opcionIndice) {
          case 1 -> {
            String name = JOptionPane.showInputDialog("Name: ");

            if (name.length() > MAX_TASK_NAME_LENGTH) {
              JOptionPane.showMessageDialog(null, LONG_TASK_NAME_WARNING);
            } else if (name.isBlank() || name.isEmpty()) {
              JOptionPane.showMessageDialog(null, "Not a valid name!");
            } else {
              taskBuilder.setName(name);
            }
          }
          case 2 -> {
            String dueDateStr = JOptionPane.showInputDialog("Due date (yyyy-MM-dd): ");

            try {
              LocalDate dueDate = LocalDate.parse(dueDateStr);
              taskBuilder.setDueDate(dueDate);
            } catch (DateTimeParseException e) {
              JOptionPane.showMessageDialog(null, "Invalid due date format");
            }
          }
          case 3 -> {
            if (taskBuilder.build().getDueDate() != null) {
              String specifiedTimeStr =
                  JOptionPane.showInputDialog("Specified time in 24h time system (hh:mm): ");

              try {
                LocalTime specifiedTime = LocalTime.parse(specifiedTimeStr);
                taskBuilder.setSpecifiedTime(specifiedTime);
              } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(null, "Invalid time format");
              }
            } else {
              JOptionPane.showMessageDialog(null, "You have to set a due date first!");
            }
          }
          case 4 -> {
            String description = JOptionPane.showInputDialog("Description: ");

            if (description.length() > 300) {
              JOptionPane.showMessageDialog(null, MAX_DESCRIPTION_LENGTH_WARNING);
            } else if (description.isBlank() || description.isEmpty()) {
              JOptionPane.showMessageDialog(null, "Not a valid description!");
            } else {
              taskBuilder.setDescription(description);
            }
          }
          case 5 -> {
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
                        title,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        relevanceOptionsArray,
                        relevanceOptionsArray[0]);

            if (relevanceStr == null) {
              JOptionPane.showMessageDialog(null, "You have to choose a level of relevance!");
            } else {
              Relevance relevance = relevanceMap.get(relevanceStr);
              taskBuilder.setRelevance(relevance);
            }
          }
          case 6 -> {
            String category = JOptionPane.showInputDialog("Category: ");

            if (!crudService.checkCategoryName(category)) {
              JOptionPane.showMessageDialog(null, "The category " + category + " doesn't exist.");
            } else {
              Category categoryObj = crudService.getCategoryByName(category);
              taskBuilder.setCategory(categoryObj);
            }
          }
          case 7 -> {
            if (taskBuilder.build().getRepeatingConfig() != null
                && (JOptionPane.showConfirmDialog(
                        null, "Do you want to set this task as not repetitive?")
                    == 0)) {
              taskBuilder.setRepeatingConfig(null);
              continue;
            }

            RepeatTaskConfig repeatingConfig = new RepeatTaskConfig();

            Map<String, RepeatType> repeatTypeMap = new HashMap<>();
            repeatTypeMap.put("Hour", RepeatType.HOUR);
            repeatTypeMap.put("Daily", RepeatType.DAILY);
            repeatTypeMap.put("Weekly", RepeatType.WEEKLY);
            repeatTypeMap.put("Monthly", RepeatType.MONTHLY);
            repeatTypeMap.put("Yearly", RepeatType.YEARLY);

            Object[] repeatTypeOptionsArray = repeatTypeMap.keySet().toArray();

            String repeatTypeStr =
                (String)
                    JOptionPane.showInputDialog(
                        null,
                        "Choose a type of repetition",
                        title,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        repeatTypeOptionsArray,
                        repeatTypeOptionsArray[0]);

            RepeatType repeatType;

            if (repeatTypeStr == null) {
              JOptionPane.showMessageDialog(null, "You have to choose a type of repetition!");
              continue;
            } else {
              repeatType = repeatTypeMap.get(repeatTypeStr);

              if (repeatType.equals(RepeatType.HOUR)
                  && taskBuilder.build().getSpecifiedTime() == null) {
                JOptionPane.showMessageDialog(
                    null,
                    "To set a task as hourly repetitive you must set first a specified time for this task!");
                continue;
              }

              repeatingConfig.setRepeatType(repeatType);
            }

            if (JOptionPane.showConfirmDialog(
                    null, "Do you want to set a end date for repetitions?")
                == 0) {
              String repeatEndsAtStr = JOptionPane.showInputDialog("Repeat ends at (yyyy-mm-dd): ");

              try {
                LocalDate repeatEndsAt = LocalDate.parse(repeatEndsAtStr);
                repeatingConfig.setRepeatEndsAt(repeatEndsAt);
              } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(null, "Invalid end date format.");
                continue;
              }
            }

            String typeStr;
            if (repeatType.equals(RepeatType.HOUR)) typeStr = "hour";
            else if (repeatType.equals(RepeatType.DAILY)) typeStr = "days";
            else if (repeatType.equals(RepeatType.WEEKLY)) typeStr = "weeks";
            else if (repeatType.equals(RepeatType.MONTHLY)) typeStr = "months";
            else typeStr = "years";

            try {
              Integer interval =
                  Integer.parseInt(JOptionPane.showInputDialog("Repeat each ? " + typeStr + ": "));
              repeatingConfig.setRepeatInterval(interval);
            } catch (NumberFormatException nfe) {
              JOptionPane.showMessageDialog(null, "Invalid interval: interval must be an integer.");
              continue;
            }

            switch (repeatType) {
              case HOUR -> {
                HourRepeatOnConfig hourRepeatOnConfig = new HourRepeatOnConfig();
                Set<Integer> minutes = new TreeSet<>();
                Integer minute;

                AtomicInteger ai = new AtomicInteger(0);
                int[] minutesOptionsArray =
                    IntStream.generate(ai::getAndIncrement).limit(60).toArray();
                Object[] minuteOptionsArrayObj =
                    Arrays.stream(minutesOptionsArray).boxed().toArray();
                boolean keep = true;

                do {
                  try {
                    minute =
                        (Integer)
                            JOptionPane.showInputDialog(
                                null,
                                "Choose a specific minute",
                                title,
                                JOptionPane.INFORMATION_MESSAGE,
                                null,
                                minuteOptionsArrayObj,
                                minuteOptionsArrayObj[0]);

                    minutes.add(minute);

                    if (JOptionPane.showConfirmDialog(null, "Do you want to add another minute?")
                        != 0) keep = false;
                  } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(null, "Invalid minute! Try again.");
                  }
                } while (keep);

                hourRepeatOnConfig.setMinutes(minutes);
                repeatingConfig.setRepeatOn(hourRepeatOnConfig);
              }
              case DAILY -> {
                DailyRepeatOnConfig dailyRepeatOnConfig = new DailyRepeatOnConfig();
                Set<LocalTime> hours = new TreeSet<>();
                LocalTime hour;
                String hourStr;

                boolean keep = true;

                do {
                  try {
                    hourStr = JOptionPane.showInputDialog("Add one specific hour (00:00 - 23:59)");
                    hour = LocalTime.parse(hourStr.trim());

                    hours.add(hour);

                    if (hours.size() == 24
                        || JOptionPane.showConfirmDialog(
                                null, "Do you want to add another hour? (24 max)")
                            != 0) keep = false;
                  } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(null, "Invalid hour! Try again.");
                  }
                } while (keep);

                dailyRepeatOnConfig.setHours(hours);
                repeatingConfig.setRepeatOn(dailyRepeatOnConfig);
              }
              case WEEKLY -> {
                WeeklyRepeatOnConfig weeklyRepeatOnConfig = new WeeklyRepeatOnConfig();
                Set<DayOfWeek> daysOfWeek = new TreeSet<>();
                DayOfWeek dayOfWeek;
                String dayOfWeekStr;

                Map<String, DayOfWeek> daysOfWeekMap = new HashMap<>();
                daysOfWeekMap.put("Monday", DayOfWeek.MONDAY);
                daysOfWeekMap.put("Tuesday", DayOfWeek.TUESDAY);
                daysOfWeekMap.put("Wednesday", DayOfWeek.WEDNESDAY);
                daysOfWeekMap.put("Thursday", DayOfWeek.THURSDAY);
                daysOfWeekMap.put("Friday", DayOfWeek.FRIDAY);
                daysOfWeekMap.put("Saturday", DayOfWeek.SATURDAY);
                daysOfWeekMap.put("Sunday", DayOfWeek.SUNDAY);

                Object[] daysOfWeekArray = daysOfWeekMap.keySet().toArray();

                boolean keep = true;

                do {
                  dayOfWeekStr =
                      (String)
                          JOptionPane.showInputDialog(
                              null,
                              "Choose a specific day of the week",
                              title,
                              JOptionPane.INFORMATION_MESSAGE,
                              null,
                              daysOfWeekArray,
                              daysOfWeekArray[0]);

                  if (dayOfWeekStr == null) {
                    JOptionPane.showMessageDialog(
                        null, "You have to choose a specific day of the week!");
                  } else {
                    dayOfWeek = daysOfWeekMap.get(dayOfWeekStr);
                    daysOfWeek.add(dayOfWeek);

                    if (JOptionPane.showConfirmDialog(
                            null, "Do you want to add another day of the week?")
                        != 0) keep = false;
                  }

                } while (keep);

                weeklyRepeatOnConfig.setDaysOfWeek(daysOfWeek);
                repeatingConfig.setRepeatOn(weeklyRepeatOnConfig);
              }
              case MONTHLY -> {
                MonthlyRepeatOnConfig monthlyRepeatOnConfig = new MonthlyRepeatOnConfig();
                Set<Integer> daysOfMonth = new TreeSet<>();
                Integer dayOfMonth;

                AtomicInteger ai = new AtomicInteger(1);
                int[] daysOfMonthOptionsArray =
                    IntStream.generate(ai::getAndIncrement).limit(31).toArray();
                Object[] daysOfMonthOptionsArrayStr =
                    Arrays.stream(daysOfMonthOptionsArray).boxed().toArray();
                boolean keep = true;

                do {
                  try {
                    dayOfMonth =
                        (Integer)
                            JOptionPane.showInputDialog(
                                null,
                                "Choose a day of the month",
                                title,
                                JOptionPane.INFORMATION_MESSAGE,
                                null,
                                daysOfMonthOptionsArrayStr,
                                daysOfMonthOptionsArrayStr[0]);

                    daysOfMonth.add(dayOfMonth);

                    if (JOptionPane.showConfirmDialog(
                            null, "Do you want to add another day of the month?")
                        != 0) keep = false;
                  } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(null, "Invalid day of the month! Try again.");
                  }
                } while (keep);

                monthlyRepeatOnConfig.setDaysOfMonth(daysOfMonth);
                repeatingConfig.setRepeatOn(monthlyRepeatOnConfig);
              }
              case YEARLY -> {
                YearlyRepeatOnConfig yearlyRepeatOnConfig = new YearlyRepeatOnConfig();
                Set<MonthDay> daysOfYear = new TreeSet<>();
                MonthDay monthDay;
                String monthDayStr;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

                boolean keep = true;

                do {
                  try {
                    monthDayStr =
                        JOptionPane.showInputDialog("Add one specific date of the year (MM-dd)");
                    monthDay = MonthDay.parse(monthDayStr.trim(), formatter);

                    daysOfYear.add(monthDay);

                    if (daysOfYear.size() == 12
                        || JOptionPane.showConfirmDialog(
                                null, "Do you want to add another date of the year? (12 max)")
                            != 0) keep = false;
                  } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(null, "Invalid date of the year! Try again.");
                  }
                } while (keep);

                yearlyRepeatOnConfig.setDaysOfYear(daysOfYear);
                repeatingConfig.setRepeatOn(yearlyRepeatOnConfig);
              }
            }

            taskBuilder.setRepeatingConfig(repeatingConfig);
          }
          case 8 -> {
            if (taskBuilder.build().getName() != null) {
              String varString = (title.contains("modification")) ? "update" : "create";

              if (JOptionPane.showConfirmDialog(
                      null,
                      "Are you sure you want to "
                          + varString
                          + " \""
                          + taskBuilder.build().getName()
                          + "\"?")
                  == 0) {
                Task newTask = taskBuilder.build();
                crudService.saveTask(newTask);
              }
            } else {
              JOptionPane.showMessageDialog(null, "You have to set a name for this task!");
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
