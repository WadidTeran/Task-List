package org.kodigo.tasklist.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import org.kodigo.tasklist.models.*;
import org.kodigo.tasklist.utils.Warnings;
import org.kodigo.tasklist.views.View;

public class TaskService {
  public static final int MAX_TASK_NAME_LENGTH = 50;
  public static final int MAX_DESCRIPTION_LENGTH = 300;
  public static final String LONG_TASK_NAME_WARNING =
      "Task names cannot be longer than " + MAX_TASK_NAME_LENGTH + " characters!";
  public static final String MAX_DESCRIPTION_LENGTH_WARNING =
      "Task description can't be longer than " + MAX_DESCRIPTION_LENGTH + " characters!";
  public static final boolean TASK_STATUS_COMPLETED = true;
  public static final boolean TASK_STATUS_PENDING = false;
  private final CRUDService crudService;
  private final AskForService askForService;
  private TaskBuilder taskBuilder;
  private RepeatTaskConfigBuilder repeatTaskConfigBuilder;

  public TaskService(CRUDService crudService, AskForService askForService) {
    this.crudService = crudService;
    this.askForService = askForService;
    askForService.setTaskService(this);
  }

  public void createTask() {
    taskBuilder = new TaskBuilder();
  }

  public boolean modifyTask() {
    if (checkExistenceOfTasks(TASK_STATUS_PENDING)) {
      try {
        Optional<Task> optTask =
            askForService.askForATask("Choose a task to modify", TASK_STATUS_PENDING);
        if (optTask.isPresent()) {
          Task taskToModify = optTask.get();
          taskBuilder = new TaskBuilder(taskToModify);
          return true;
        }
      } catch (NumberFormatException e) {
        View.message(Warnings.NOT_VALID_TASK_ID);
      }
    } else {
      View.message(Warnings.NO_PENDING_TASKS);
    }
    return false;
  }

  public void processName() {
    String name = View.input("Name");
    if (name == null) return;
    else if (name.isBlank() || name.isEmpty()) {
      View.message(Warnings.EMPTY_INPUT);
      return;
    }
    name = name.trim();

    if (name.length() > MAX_TASK_NAME_LENGTH) {
      View.message(LONG_TASK_NAME_WARNING);
    } else if (name.isBlank() || name.isEmpty()) {
      View.message(Warnings.EMPTY_INPUT);
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
      View.message(Warnings.EMPTY_INPUT);
      return;
    }
    dueDateStr = dueDateStr.trim();

    try {
      LocalDate dueDate = LocalDate.parse(dueDateStr);
      taskBuilder.setDueDate(dueDate);
    } catch (DateTimeParseException e) {
      View.message(Warnings.INVALID_FORMAT);
    }
  }

  public void processSpecifiedTime() {
    if (taskBuilder.build().getDueDate() != null) {
      String specifiedTimeStr = View.input("Specified time in 24h time system (hh:mm)");
      if (specifiedTimeStr == null) return;
      else if (specifiedTimeStr.isBlank() || specifiedTimeStr.isEmpty()) {
        View.message(Warnings.EMPTY_INPUT);
        return;
      }
      specifiedTimeStr = specifiedTimeStr.trim();

      try {
        LocalTime specifiedTime = LocalTime.parse(specifiedTimeStr);
        taskBuilder.setSpecifiedTime(specifiedTime);
      } catch (DateTimeParseException e) {
        View.message(Warnings.INVALID_FORMAT);
      }
    } else {
      View.message(Warnings.SET_DUE_DATE_FIRST);
    }
  }

  public void processDescription() {
    String description = View.input("Description");
    if (description == null) return;
    else if (description.isBlank() || description.isEmpty()) {
      View.message(Warnings.EMPTY_INPUT);
      return;
    }
    description = description.trim();

    if (description.length() > 300) {
      View.message(MAX_DESCRIPTION_LENGTH_WARNING);
    } else if (description.isBlank() || description.isEmpty()) {
      View.message(Warnings.EMPTY_INPUT);
    } else {
      taskBuilder.setDescription(description);
    }
  }

  public void processRelevance() {
    Optional<Relevance> relevanceOptional = askForService.askForARelevance();
    relevanceOptional.ifPresent(relevance -> taskBuilder.setRelevance(relevance));
  }

  public void processCategory() {
    Optional<Category> categoryOptional = askForService.askForACategory();
    categoryOptional.ifPresent(category -> taskBuilder.setCategory(category));
  }

  public boolean processRepeatConfig() {
    if (taskBuilder.build().getDueDate() == null) {
      View.message(Warnings.SET_DUE_DATE_FIRST);
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
    Optional<RepeatType> repeatTypeOptional = askForService.askForARepeatType();
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
      View.message(Warnings.EMPTY_INPUT);
      return;
    }
    repeatEndsAtStr = repeatEndsAtStr.trim();

    try {
      LocalDate repeatEndsAt = LocalDate.parse(repeatEndsAtStr);
      repeatTaskConfigBuilder.setRepeatEndsAt(repeatEndsAt);
    } catch (DateTimeParseException e) {
      View.message(Warnings.INVALID_FORMAT);
    }
  }

  public void processRepeatInterval() {
    if (repeatTaskConfigBuilder.build().getRepeatType() == null) {
      View.message(Warnings.SET_A_REPEAT_TYPE_FIRST);
    } else {
      String intervalStr;
      int interval;

      try {
        intervalStr =
            View.input("Repeat each ? " + repeatTaskConfigBuilder.build().getRepeatType());
        if (intervalStr == null) return;
        else if (intervalStr.isBlank() || intervalStr.isEmpty()) {
          View.message(Warnings.EMPTY_INPUT);
          return;
        }
        intervalStr = intervalStr.trim();

        interval = Integer.parseInt(intervalStr);

        if (interval > 0) repeatTaskConfigBuilder.setRepeatInterval(interval);
        else View.message("Invalid repeat interval");
      } catch (NumberFormatException nfe) {
        View.message(Warnings.INVALID_FORMAT);
      }
    }
  }

  public void processRepeatOn() {
    if (repeatTaskConfigBuilder.build().getRepeatType() == null) {
      View.message(Warnings.SET_A_REPEAT_TYPE_FIRST);
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
    Integer minute;

    do {
      minute = askForService.askForAMinute();
      if (minute == null) {
        if (minutes.isEmpty()) return;
        else break;
      }
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
          View.message(Warnings.EMPTY_INPUT);
          continue;
        }

        hour = LocalTime.parse(hourStr.trim());
        hours.add(hour);

        if (hours.size() == 24 || !View.confirm("Do you want to add another hour? (24 max)"))
          keep = false;
      } catch (DateTimeParseException e) {
        View.message(Warnings.INVALID_FORMAT);
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

    do {
      dayOfWeek = askForService.askForADayOfWeek();
      if (dayOfWeek == null) {
        if (daysOfWeek.isEmpty()) return;
        else break;
      }
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
    Integer dayOfMonth;

    do {
      dayOfMonth = askForService.askForADayOfMonth();
      if (dayOfMonth == null) {
        if (daysOfMonth.isEmpty()) return;
        else break;
      }
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
          View.message(Warnings.EMPTY_INPUT);
          return;
        }

        monthDay = MonthDay.parse(monthDayStr.trim(), formatter);
        daysOfYear.add(monthDay);

        if (daysOfYear.size() == 12
            || !View.confirm("Do you want to add another date of the year? (12 max)")) keep = false;
      } catch (DateTimeParseException e) {
        View.message(Warnings.INVALID_FORMAT);
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
            askForService.askForATask("Choose a task to set as completed", TASK_STATUS_PENDING);
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
        View.message(Warnings.NOT_A_NUMBER);
      }
    } else {
      View.message(Warnings.NO_PENDING_TASKS);
    }
  }

  public void setAsPendingTask() {
    if (checkExistenceOfTasks(TASK_STATUS_COMPLETED)) {
      try {
        Optional<Task> optionalTask =
            askForService.askForATask("Choose a task to set as pending", TASK_STATUS_COMPLETED);
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
        View.message(Warnings.NOT_A_NUMBER);
      }
    } else {
      View.message(Warnings.NO_COMPLETED_TASKS);
    }
  }

  public void deleteTask() {
    if (checkExistenceOfTasks(TASK_STATUS_PENDING)) {
      try {
        Optional<Task> optionalTask =
            askForService.askForATask("Choose a task to delete", TASK_STATUS_PENDING);
        optionalTask.ifPresent(
            task -> {
              if (View.confirm("Are you sure you want to delete \"" + task.getName() + "\"?"))
                crudService.deleteTask(task);
            });
      } catch (NumberFormatException e) {
        View.message(Warnings.NOT_A_NUMBER);
      }
    } else {
      View.message(Warnings.NO_PENDING_TASKS);
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
      View.message(Warnings.NO_PENDING_TASKS);
    } else {
      View.displayAllPendingTasks(pendingTasks);
    }
  }

  public void searchTasksByRelevance() {
    if (!checkExistenceOfTasks(TASK_STATUS_PENDING)) {
      View.message(Warnings.NO_PENDING_TASKS);
    } else {
      Optional<Relevance> relevanceOptional = askForService.askForARelevance();
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
      View.message(Warnings.NO_PENDING_TASKS);
    } else if (crudService.findAllCategories().isEmpty()) {
      View.message(Warnings.NO_EXISTING_CATEGORIES);
    } else {
      Optional<Category> categoryOptional = askForService.askForACategory();
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
      View.message(Warnings.NO_COMPLETED_TASKS);
    } else {
      View.displayCompletedTasks(completedTasks);
    }
  }

  public void deleteCompletedTasks() {
    List<Task> completedTasks = getCompletedTasks();
    if (completedTasks.isEmpty()) {
      View.message(Warnings.NO_COMPLETED_TASKS);
    } else if (View.confirm("Are you sure you want to delete all completed tasks?")) {
      for (Task completedTask : completedTasks) {
        crudService.deleteTask(completedTask);
      }
    }
  }

  public void searchOneTask() {
    if (checkExistenceOfTasks(TASK_STATUS_PENDING)) {
      try {
        Optional<Task> optTask =
            askForService.askForATask("Choose a task to search", TASK_STATUS_PENDING);
        optTask.ifPresent(View::displayOneTask);
      } catch (NumberFormatException e) {
        View.message(Warnings.NOT_VALID_TASK_ID);
      }
    } else {
      View.message(Warnings.NO_PENDING_TASKS);
    }
  }

  public List<Task> getCompletedTasks() {
    return crudService.findAllTasks().stream()
        .filter(Task::isCompleted)
        .collect(Collectors.toList());
  }

  public List<Task> getAllPendingTasks() {
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

  private boolean checkExistenceOfTasks(boolean taskStatus) {
    List<Task> tasks =
        (taskStatus == TASK_STATUS_COMPLETED) ? getCompletedTasks() : getAllPendingTasks();
    return !tasks.isEmpty();
  }

  private boolean checkTaskName(String taskName) {
    return crudService.findAllTasks().stream().anyMatch(t -> t.getName().equals(taskName));
  }
}
