package services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import models.*;
import utils.AskForMethodsDataContainer;
import utils.RangeDates;
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
  private static final String EMPTY_MESSAGE_WARNING = "No empty entries allowed. Please, try again";
  private static final String NOT_A_NUMBER = "That is not a number.";
  private static final boolean TASK_STATUS_COMPLETED = true;
  private static final boolean TASK_STATUS_PENDING = false;
  private final CRUDService crudService;
  private final CategoryService categoryService;
  private TaskBuilder taskBuilder;
  private RepeatTaskConfigBuilder repeatTaskConfigBuilder;

  public TaskService(CRUDService crudService, CategoryService categoryService) {
    this.crudService = crudService;
    this.categoryService = categoryService;
  }

  public void createTask() {
    taskBuilder = new TaskBuilder();
  }

  public boolean modifyTask() {
    if (checkExistenceOfTasks(TASK_STATUS_PENDING)) {
      try {
        Optional<Task> optTask = askForATask("Choose a task to modify", TASK_STATUS_PENDING);
        if (optTask.isPresent()) {
          Task taskToModify = optTask.get();
          taskBuilder = new TaskBuilder(taskToModify);
          return true;
        }
      } catch (NumberFormatException e) {
        View.message("Not a valid task id.");
      }
    } else {
      View.message(NO_PENDING_TASKS_WARNING);
    }
    return false;
  }

  public void processName() {
    String name = View.input("Name");
    if (name == null) return;
    else if (name.isBlank() || name.isEmpty()) {
      View.message(EMPTY_MESSAGE_WARNING);
      return;
    }
    name = name.trim();

    if (name.length() > MAX_TASK_NAME_LENGTH) {
      View.message(LONG_TASK_NAME_WARNING);
    } else if (name.isBlank() || name.isEmpty()) {
      View.message("Not a valid name!");
    } else if (checkTaskName(name)) {
      View.message("A task with this name already exists!");
    } else {
      taskBuilder.setName(name);
    }
  }

  public void processDueDate() {
    String dueDateStr = View.input("Due date (yyyy-MM-dd)");
    if (dueDateStr == null) return;
    else if (dueDateStr.isBlank() || dueDateStr.isEmpty()) {
      View.message(EMPTY_MESSAGE_WARNING);
      return;
    }
    dueDateStr = dueDateStr.trim();

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
      if (specifiedTimeStr == null) return;
      else if (specifiedTimeStr.isBlank() || specifiedTimeStr.isEmpty()) {
        View.message(EMPTY_MESSAGE_WARNING);
        return;
      }
      specifiedTimeStr = specifiedTimeStr.trim();

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
    if (description == null) return;
    else if (description.isBlank() || description.isEmpty()) {
      View.message(EMPTY_MESSAGE_WARNING);
      return;
    }
    description = description.trim();

    if (description.length() > 300) {
      View.message(MAX_DESCRIPTION_LENGTH_WARNING);
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

  public boolean processRepeatConfig() {
    if (taskBuilder.build().getDueDate() == null) {
      View.message("You have to set a due date first!");
    } else if (taskBuilder.build().getRepeatingConfig() != null
        && (View.confirm("Do you want to set this task as not repetitive?"))) {
      taskBuilder.setRepeatingConfig(null);
    } else {
      if (taskBuilder.build().getRepeatingConfig() != null) {
        repeatTaskConfigBuilder =
            new RepeatTaskConfigBuilder(taskBuilder.build().getRepeatingConfig());
      } else {
        repeatTaskConfigBuilder = new RepeatTaskConfigBuilder();
      }
      return true;
    }
    return false;
  }

  public void processRepeatType() {
    Optional<RepeatType> repeatTypeOptional = askForARepeatType();
    if (repeatTypeOptional.isPresent()) {
      RepeatType repeatType = repeatTypeOptional.get();
      if (repeatType.equals(RepeatType.HOUR) && taskBuilder.build().getSpecifiedTime() == null) {
        View.message(
            "To set a task as hourly repetitive you must set first a specified time for this task!");
      } else {
        repeatTaskConfigBuilder.setRepeatType(repeatType);
      }
    }
  }

  public void processRepeatEndsAt() {
    String repeatEndsAtStr = View.input("Repeat ends at (yyyy-mm-dd)");
    if (repeatEndsAtStr == null) return;
    else if (repeatEndsAtStr.isBlank() || repeatEndsAtStr.isEmpty()) {
      View.message(EMPTY_MESSAGE_WARNING);
      return;
    }
    repeatEndsAtStr = repeatEndsAtStr.trim();

    try {
      LocalDate repeatEndsAt = LocalDate.parse(repeatEndsAtStr);
      repeatTaskConfigBuilder.setRepeatEndsAt(repeatEndsAt);
    } catch (DateTimeParseException e) {
      View.message("Invalid end date format.");
    }
  }

  public void processRepeatInterval() {
    if (repeatTaskConfigBuilder.build().getRepeatType() == null) {
      View.message("First set a repeat type!");
    } else {
      String intervalStr;
      int interval;

      try {
        intervalStr =
            View.input("Repeat each ? " + repeatTaskConfigBuilder.build().getRepeatType());
        if (intervalStr == null) return;
        else if (intervalStr.isBlank() || intervalStr.isEmpty()) {
          View.message(EMPTY_MESSAGE_WARNING);
          return;
        }
        intervalStr = intervalStr.trim();

        interval = Integer.parseInt(intervalStr);

        if (interval > 0) repeatTaskConfigBuilder.setRepeatInterval(interval);
        else View.message("Invalid repeat interval");
      } catch (NumberFormatException nfe) {
        View.message("Invalid interval: interval must be an integer.");
      }
    }
  }

  public void processRepeatOn() {
    if (repeatTaskConfigBuilder.build().getRepeatType() == null) {
      View.message("First set a repeat type!");
    } else {
      switch (repeatTaskConfigBuilder.build().getRepeatType()) {
        case HOUR -> processRepeatOnHour();
        case DAILY -> processRepeatOnDaily();
        case WEEKLY -> processRepeatOnWeekly();
        case MONTHLY -> processRepeatOnMonthly();
        case YEARLY -> processRepeatOnYearly();
      }
    }
  }

  public void processRepeatOnHour() {
    boolean keep = true;
    HourRepeatOnConfig hourRepeatOnConfig = new HourRepeatOnConfig();
    Set<Integer> minutes = new TreeSet<>();
    String minuteStr;
    int minute;

    do {
      minuteStr =
          View.inputOptions(
              "Minute selector",
              "Choose a specific minute",
              AskForMethodsDataContainer.getMinuteOptionsArrayObj());
      if (minuteStr == null) {
        if (minutes.isEmpty()) return;
        else break;
      }
      minute = Integer.parseInt(minuteStr);
      minutes.add(minute);

      if (!View.confirm("Do you want to add another minute?")) keep = false;
    } while (keep);

    hourRepeatOnConfig.setMinutes(minutes);
    repeatTaskConfigBuilder.setRepeatOnConfig(hourRepeatOnConfig);
  }

  public void processRepeatOnDaily() {
    boolean keep = true;
    DailyRepeatOnConfig dailyRepeatOnConfig = new DailyRepeatOnConfig();
    Set<LocalTime> hours = new TreeSet<>();
    LocalTime hour;
    String hourStr;

    do {
      try {
        hourStr = View.input("Add one specific hour (00:00 - 23:59)");
        if (hourStr == null) {
          if (hours.isEmpty()) return;
          else break;
        } else if (hourStr.isBlank() || hourStr.isEmpty()) {
          View.message(EMPTY_MESSAGE_WARNING);
          continue;
        }

        hour = LocalTime.parse(hourStr.trim());
        hours.add(hour);

        if (hours.size() == 24 || !View.confirm("Do you want to add another hour? (24 max)"))
          keep = false;
      } catch (DateTimeParseException e) {
        View.message("Invalid hour! Try again.");
      }
    } while (keep);

    dailyRepeatOnConfig.setHours(hours);
    repeatTaskConfigBuilder.setRepeatOnConfig(dailyRepeatOnConfig);
  }

  public void processRepeatOnWeekly() {
    boolean keep = true;
    WeeklyRepeatOnConfig weeklyRepeatOnConfig = new WeeklyRepeatOnConfig();
    Set<DayOfWeek> daysOfWeek = new TreeSet<>();
    DayOfWeek dayOfWeek;
    String dayOfWeekStr;

    do {
      dayOfWeekStr =
          View.inputOptions(
              "Day of week selector",
              "Choose a specific day of the week",
              AskForMethodsDataContainer.getDaysOfWeekArray());
      if (dayOfWeekStr == null) {
        if (daysOfWeek.isEmpty()) return;
        else break;
      }

      dayOfWeek = AskForMethodsDataContainer.getDaysOfWeekMap().get(dayOfWeekStr);
      daysOfWeek.add(dayOfWeek);

      if (!View.confirm("Do you want to add another day of the week?")) keep = false;
    } while (keep);

    weeklyRepeatOnConfig.setDaysOfWeek(daysOfWeek);
    repeatTaskConfigBuilder.setRepeatOnConfig(weeklyRepeatOnConfig);
  }

  public void processRepeatOnMonthly() {
    boolean keep = true;
    MonthlyRepeatOnConfig monthlyRepeatOnConfig = new MonthlyRepeatOnConfig();
    Set<Integer> daysOfMonth = new TreeSet<>();
    String dayOfMonthStr;
    int dayOfMonth;

    do {
      dayOfMonthStr =
          View.inputOptions(
              "Day of month selector",
              "Choose a day of the month",
              AskForMethodsDataContainer.getDaysOfMonthOptionsArrayStr());
      if (dayOfMonthStr == null) {
        if (daysOfMonth.isEmpty()) return;
        else break;
      }

      dayOfMonth = Integer.parseInt(dayOfMonthStr);
      daysOfMonth.add(dayOfMonth);

      if (!View.confirm("Do you want to add another day of the month?")) keep = false;
    } while (keep);

    monthlyRepeatOnConfig.setDaysOfMonth(daysOfMonth);
    repeatTaskConfigBuilder.setRepeatOnConfig(monthlyRepeatOnConfig);
  }

  public void processRepeatOnYearly() {
    boolean keep = true;
    YearlyRepeatOnConfig yearlyRepeatOnConfig = new YearlyRepeatOnConfig();
    Set<MonthDay> daysOfYear = new TreeSet<>();
    MonthDay monthDay;
    String monthDayStr;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

    do {
      try {
        monthDayStr = View.input("Add one specific date of the year (MM-dd)");
        if (monthDayStr == null) {
          if (daysOfYear.isEmpty()) return;
          else break;
        } else if (monthDayStr.isBlank() || monthDayStr.isEmpty()) {
          View.message(EMPTY_MESSAGE_WARNING);
          return;
        }

        monthDay = MonthDay.parse(monthDayStr.trim(), formatter);
        daysOfYear.add(monthDay);

        if (daysOfYear.size() == 12
            || !View.confirm("Do you want to add another date of the year? (12 max)")) keep = false;
      } catch (DateTimeParseException e) {
        View.message("Invalid date of the year! Try again.");
      }
    } while (keep);

    yearlyRepeatOnConfig.setDaysOfYear(daysOfYear);
    repeatTaskConfigBuilder.setRepeatOnConfig(yearlyRepeatOnConfig);
  }

  public boolean finishRepeatConfigProcess() {
    if (repeatTaskConfigBuilder.isValidToBuild()) {
      taskBuilder.setRepeatingConfig(repeatTaskConfigBuilder.build());
      return true;
    }
    return View.confirm("Repeat configuration is not finished, do you want to cancel the process?");
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
        optionalTask.ifPresent(
            task -> {
              if (View.confirm(
                  "Are you sure you want to set as completed \"" + task.getName() + "\"?")) {
                task.setCompleted(true);
                task.setCompletedDate(LocalDate.now());
                crudService.saveTask(task);
                if (task.isRepetitive()) {
                  manageRepetitiveTask(task);
                }
              }
            });
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
          Task taskToSet = optionalTask.get();
          if (View.confirm(
              "Are you sure you want to set as pending \"" + taskToSet.getName() + "\"?")) {
            taskToSet.setCompleted(false);
            taskToSet.setCompletedDate(null);
            crudService.saveTask(taskToSet);
          }
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
        optionalTask.ifPresent(
            task -> {
              if (View.confirm("Are you sure you want to delete \"" + task.getName() + "\"?"))
                crudService.deleteTask(task);
            });
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
        optTask.ifPresent(View::displayOneTask);
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

    Object[] taskArray = tasks.stream().map(Task::getName).toArray();

    String task = View.inputOptions("Task selector", message, taskArray);

    if (task == null) {
      return Optional.empty();
    } else {
      return tasks.stream().filter(t -> t.getName().equals(task)).findFirst();
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
        View.inputOptions(
            "Relevance selector",
            "Choose the relevance",
            AskForMethodsDataContainer.getRelevanceArray());

    if (relevance == null) {
      return Optional.empty();
    } else {
      return Optional.of(AskForMethodsDataContainer.getRelevanceMap().get(relevance));
    }
  }

  private Optional<RepeatType> askForARepeatType() {
    String repeatType =
        View.inputOptions(
            "Repeat Type selector",
            "Choose the repeat type",
            AskForMethodsDataContainer.getRepeatTypeArray());

    if (repeatType == null) {
      return Optional.empty();
    } else {
      return Optional.of(AskForMethodsDataContainer.getRepeatTypeMap().get(repeatType));
    }
  }

  public void sendProductivityEmail() {
    String startDateStr = View.input("Start date (yyyy-MM-dd)");
    if (startDateStr == null) return;
    else if (startDateStr.isBlank() || startDateStr.isEmpty()) {
      View.message(EMPTY_MESSAGE_WARNING);
      return;
    }
    String endDateStr = View.input("End date (yyyy-MM-dd)");
    if (endDateStr == null) return;
    else if (endDateStr.isBlank() || endDateStr.isEmpty()) {
      View.message(EMPTY_MESSAGE_WARNING);
      return;
    }

    try {
      LocalDate startDate = LocalDate.parse(startDateStr);
      LocalDate endDate = LocalDate.parse(endDateStr);
      if (!startDate.isAfter(endDate)) {
        RangeDates rangeDates = new RangeDates(startDate, endDate);
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
          Runnable emailSendingProcess =
              () -> ProductivityEmailService.sendProductivityEmail(this, rangeDates);
          executor.execute(emailSendingProcess);
        }
      } else {
        View.message("¡Start date must be before end date!");
      }
    } catch (DateTimeParseException e) {
      View.message("Invalid date format");
    }
  }

  public boolean checkTaskName(String taskName) {
    return crudService.findAllTasks().stream().anyMatch(t -> t.getName().equals(taskName));
  }
}
