package services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
  private final Map<String, DayOfWeek> daysOfWeekMap;
  private final Object[] daysOfWeekArray;
  private final Object[] minuteOptionsArrayObj;
  private final Object[] daysOfMonthOptionsArrayStr;
  private TaskBuilder taskBuilder;

  public TaskService(ICRUDService crudService, CategoryService categoryService) {
    this.crudService = crudService;
    this.categoryService = categoryService;

    repeatTypeMap = new LinkedHashMap<>();
    repeatTypeMap.put("HOUR", RepeatType.HOUR);
    repeatTypeMap.put("DAILY", RepeatType.DAILY);
    repeatTypeMap.put("WEEKLY", RepeatType.WEEKLY);
    repeatTypeMap.put("MONTHLY", RepeatType.MONTHLY);
    repeatTypeMap.put("YEARLY", RepeatType.YEARLY);
    repeatTypeArray = repeatTypeMap.keySet().toArray();

    relevanceMap = new LinkedHashMap<>();
    relevanceMap.put("NONE", Relevance.NONE);
    relevanceMap.put("LOW", Relevance.LOW);
    relevanceMap.put("MEDIUM", Relevance.MEDIUM);
    relevanceMap.put("HIGH", Relevance.HIGH);
    relevanceArray = relevanceMap.keySet().toArray();

    daysOfWeekMap = new LinkedHashMap<>();
    daysOfWeekMap.put("MONDAY", DayOfWeek.MONDAY);
    daysOfWeekMap.put("TUESDAY", DayOfWeek.TUESDAY);
    daysOfWeekMap.put("WEDNESDAY", DayOfWeek.WEDNESDAY);
    daysOfWeekMap.put("THURSDAY", DayOfWeek.THURSDAY);
    daysOfWeekMap.put("FRIDAY", DayOfWeek.FRIDAY);
    daysOfWeekMap.put("SATURDAY", DayOfWeek.SATURDAY);
    daysOfWeekMap.put("SUNDAY", DayOfWeek.SUNDAY);
    daysOfWeekArray = daysOfWeekMap.keySet().toArray();

    AtomicInteger ai = new AtomicInteger(0);
    int[] minutesOptionsArray = IntStream.generate(ai::getAndIncrement).limit(60).toArray();
    minuteOptionsArrayObj = Arrays.stream(minutesOptionsArray).boxed().toArray();

    ai = new AtomicInteger(1);
    int[] daysOfMonthOptionsArray = IntStream.generate(ai::getAndIncrement).limit(31).toArray();
    daysOfMonthOptionsArrayStr = Arrays.stream(daysOfMonthOptionsArray).boxed().toArray();
  }

  public void createTask() {
    taskBuilder = new TaskBuilder();
  }

  public void modifyTask() {
    if (checkExistenceOfTasks(TASK_STATUS_PENDING)) {
      try {
        Optional<Task> optTask = askForATask("Choose a task to modify", TASK_STATUS_PENDING);
        if (optTask.isPresent()) {
          Task taskToModify = optTask.get();
          taskBuilder = new TaskBuilder(taskToModify);
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

  public void processName() {
    String name = View.input("Name");

    if (name.length() > TaskService.MAX_TASK_NAME_LENGTH) {
      View.message(TaskService.LONG_TASK_NAME_WARNING);
    } else if (name.isBlank() || name.isEmpty()) {
      View.message("Not a valid name!");
    } else {
      taskBuilder.setName(name);
    }
  }

  public void processDueDate() {
    String dueDateStr = View.input("Due date (yyyy-MM-dd)");

    try {
      LocalDate dueDate = LocalDate.parse(dueDateStr);
      taskBuilder.setDueDate(dueDate);
    } catch (DateTimeParseException e) {
      View.message("Invalid due date format");
    }
  }

  public void processSpecifiedTime() {
    if (taskBuilder.build().getDueDate() != null) {
      String specifiedTimeStr = View.input("Specified time in 24h time system (hh:mm)");

      try {
        LocalTime specifiedTime = LocalTime.parse(specifiedTimeStr);
        taskBuilder.setSpecifiedTime(specifiedTime);
      } catch (DateTimeParseException e) {
        View.message("Invalid time format");
      }
    } else {
      View.message("You have to set a due date first!");
    }
  }

  public void processDescription() {
    String description = View.input("Description");

    if (description.length() > 300) {
      View.message(TaskService.MAX_DESCRIPTION_LENGTH_WARNING);
    } else if (description.isBlank() || description.isEmpty()) {
      View.message("Not a valid description!");
    } else {
      taskBuilder.setDescription(description);
    }
  }

  public void processRelevance() {
    Optional<Relevance> relevanceOptional = askForARelevance();
    relevanceOptional.ifPresent(relevance -> taskBuilder.setRelevance(relevance));
  }

  public void processCategory() {
    Optional<Category> categoryOptional = categoryService.askForACategory();
    categoryOptional.ifPresent(category -> taskBuilder.setCategory(category));
  }

  public void processRepeatConfig() {
    if (taskBuilder.build().getDueDate() == null) {
      View.message("You have to set a due date first!");
    } else if (taskBuilder.build().getRepeatingConfig() != null
        && (View.confirm("Do you want to set this task as not repetitive?"))) {
      taskBuilder.setRepeatingConfig(null);
    } else {
      Optional<RepeatType> repeatTypeOptional = askForARepeatType();
      if (repeatTypeOptional.isPresent()) {
        RepeatTaskConfig repeatingConfig = new RepeatTaskConfig();
        RepeatType repeatType = repeatTypeOptional.get();
        if (repeatType.equals(RepeatType.HOUR) && taskBuilder.build().getSpecifiedTime() == null) {
          View.message(
              "To set a task as hourly repetitive you must set first a specified time for this task!");
          return;
        }

        repeatingConfig.setRepeatType(repeatType);

        if (View.confirm("Do you want to set a end date for repetitions?")) {
          String repeatEndsAtStr = View.input("Repeat ends at (yyyy-mm-dd)");

          try {
            LocalDate repeatEndsAt = LocalDate.parse(repeatEndsAtStr);
            repeatingConfig.setRepeatEndsAt(repeatEndsAt);
          } catch (DateTimeParseException e) {
            View.message("Invalid end date format.");
            return;
          }
        }

        try {
          Integer interval = Integer.parseInt(View.input("Repeat each ? " + repeatType));
          repeatingConfig.setRepeatInterval(interval);
        } catch (NumberFormatException nfe) {
          View.message("Invalid interval: interval must be an integer.");
          return;
        }

        boolean keep = true;

        switch (repeatType) {
          case HOUR -> {
            HourRepeatOnConfig hourRepeatOnConfig = new HourRepeatOnConfig();
            Set<Integer> minutes = new TreeSet<>();
            int minute;

            do {
              try {
                minute =
                    Integer.parseInt(
                        View.inputOptions(
                            "Minute selector", "Choose a specific minute", minuteOptionsArrayObj));
                minutes.add(minute);

                if (!View.confirm("Do you want to add another minute?")) keep = false;
              } catch (NumberFormatException nfe) {
                if (minutes.isEmpty()) {
                  View.message("You have to specify at least one minute!");
                } else {
                  keep = false;
                }
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

            do {
              try {
                hourStr = View.input("Add one specific hour (00:00 - 23:59)");
                hour = LocalTime.parse(hourStr.trim());

                hours.add(hour);

                if (hours.size() == 24
                    || !View.confirm("Do you want to add another hour? (24 max)")) keep = false;
              } catch (DateTimeParseException e) {
                View.message("Invalid hour! Try again.");
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

            do {
              dayOfWeekStr =
                  View.inputOptions(
                      "Day of week selector", "Choose a specific day of the week", daysOfWeekArray);

              if (dayOfWeekStr == null) {
                View.message("You have to choose a specific day of the week!");
              } else {
                dayOfWeek = daysOfWeekMap.get(dayOfWeekStr);
                daysOfWeek.add(dayOfWeek);

                if (!View.confirm("Do you want to add another day of the week?")) keep = false;
              }

            } while (keep);

            weeklyRepeatOnConfig.setDaysOfWeek(daysOfWeek);
            repeatingConfig.setRepeatOn(weeklyRepeatOnConfig);
          }
          case MONTHLY -> {
            MonthlyRepeatOnConfig monthlyRepeatOnConfig = new MonthlyRepeatOnConfig();
            Set<Integer> daysOfMonth = new TreeSet<>();
            int dayOfMonth;

            do {
              try {
                dayOfMonth =
                    Integer.parseInt(
                        View.inputOptions(
                            "Day of month selector",
                            "Choose a day of the month",
                            daysOfMonthOptionsArrayStr));

                daysOfMonth.add(dayOfMonth);

                if (!View.confirm("Do you want to add another day of the month?")) keep = false;
              } catch (NumberFormatException nfe) {
                View.message("Invalid day of the month! Try again.");
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

            do {
              try {
                monthDayStr = View.input("Add one specific date of the year (MM-dd)");
                monthDay = MonthDay.parse(monthDayStr.trim(), formatter);

                daysOfYear.add(monthDay);

                if (daysOfYear.size() == 12
                    || !View.confirm("Do you want to add another date of the year? (12 max)"))
                  keep = false;
              } catch (DateTimeParseException e) {
                View.message("Invalid date of the year! Try again.");
              }
            } while (keep);

            yearlyRepeatOnConfig.setDaysOfYear(daysOfYear);
            repeatingConfig.setRepeatOn(yearlyRepeatOnConfig);
          }
        }

        taskBuilder.setRepeatingConfig(repeatingConfig);
      }
    }
  }

  public boolean finishProcess(String processType) {
    if (taskBuilder.build().getName() != null) {
      if (View.confirm(
          "Are you sure you want to "
              + processType
              + " \""
              + taskBuilder.build().getName()
              + "\"?")) {
        Task newTask = taskBuilder.build();
        crudService.saveTask(newTask);
        return true;
      }
    } else {
      View.message("You have to set a name for this task!");
    }
    return false;
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
