package controllers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JOptionPane;
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
    searchAllPendingTasks();
    View.display("Insert task's id: ");
    try {
      Long taskId = scanner.nextLong();
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
        View.display("The task's id doesn't exist!");
      }
    } catch (InputMismatchException e) {
      View.display("Not a valid task id.");
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
            View.display("Due date (yyyy-MM-dd): ");
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

            if (repeatType.equals(RepeatType.HOUR)
                && taskBuilder.build().getSpecifiedTime() == null) {
              View.display(
                  "To set a task as hourly repetitive you must set first a specified time for this task!");
              continue;
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
                View.display("Invalid end date format.");
                continue;
              }
            }

            String typeStr;

            if (repeatType.equals(RepeatType.HOUR)) typeStr = "hour";
            else if (repeatType.equals(RepeatType.DAILY)) typeStr = "days";
            else if (repeatType.equals(RepeatType.WEEKLY)) typeStr = "weeks";
            else if (repeatType.equals(RepeatType.MONTHLY)) typeStr = "months";
            else typeStr = "years";

            View.display("Repeat each ? " + typeStr + ": ");
            try {
              Integer interval = scanner.nextInt();
              repeatingConfig.setRepeatInterval(interval);
            } catch (InputMismatchException ime) {
              View.display("Invalid interval: interval must be an integer.");
              continue;
            }

            switch (repeatType) {
              case HOUR -> {
                HourRepeatOnConfig hourRepeatOnConfig = new HourRepeatOnConfig();
                Set<Integer> minutes = new TreeSet<>();
                int minute;

                String oneMore;
                boolean keep = true;

                do {
                  try {
                    View.display("Add one specified minute (0 - 59): ");
                    minute = scanner.nextInt();

                    if (minute > 59 || minute < 0) {
                      View.display("Minute value must be between 0 and 59! Try again.");
                      continue;
                    }

                    minutes.add(minute);

                    View.display("Do you want to add another minute (Y/N): ");
                    oneMore = scanner.nextLine();

                    if (!oneMore.equalsIgnoreCase("Y")) keep = false;
                  } catch (InputMismatchException e) {
                    View.display("Invalid minute! Try again.");
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

                String oneMore;
                boolean keep = true;

                do {
                  try {
                    View.display("Add one specific hour (00:00 - 23:59): ");
                    hourStr = scanner.nextLine();
                    hour = LocalTime.parse(hourStr.trim());

                    hours.add(hour);

                    View.display("Do you want to add another hour (Y/N): ");
                    oneMore = scanner.nextLine();

                    if (!oneMore.equalsIgnoreCase("Y")) keep = false;
                  } catch (DateTimeParseException e) {
                    View.display("Invalid hour! Try again.");
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

                String oneMore;
                boolean keep = true;

                do {
                  View.display("Add one specific day of the week (M, TU, W, TH, F, SA, SU): ");
                  dayOfWeekStr = scanner.nextLine();

                  switch (dayOfWeekStr.trim()) {
                    case "M" -> dayOfWeek = DayOfWeek.MONDAY;
                    case "TU" -> dayOfWeek = DayOfWeek.TUESDAY;
                    case "W" -> dayOfWeek = DayOfWeek.WEDNESDAY;
                    case "TH" -> dayOfWeek = DayOfWeek.THURSDAY;
                    case "F" -> dayOfWeek = DayOfWeek.FRIDAY;
                    case "SA" -> dayOfWeek = DayOfWeek.SATURDAY;
                    case "SU" -> dayOfWeek = DayOfWeek.SUNDAY;
                    default -> {
                      View.display("Not a valid day of the week! Try again.");
                      continue;
                    }
                  }

                  daysOfWeek.add(dayOfWeek);

                  View.display("Do you want to add another day of the week (Y/N): ");
                  oneMore = scanner.nextLine();

                  if (!oneMore.equalsIgnoreCase("Y")) keep = false;
                } while (keep);

                weeklyRepeatOnConfig.setDaysOfWeek(daysOfWeek);
                repeatingConfig.setRepeatOn(weeklyRepeatOnConfig);
              }
              case MONTHLY -> {
                MonthlyRepeatOnConfig monthlyRepeatOnConfig = new MonthlyRepeatOnConfig();
                Set<Integer> daysOfMonth = new TreeSet<>();
                int dayOfMonth;

                String oneMore;
                boolean keep = true;

                do {
                  try {
                    View.display("Add one specified day of the month (1 - 31): ");
                    dayOfMonth = scanner.nextInt();

                    if (dayOfMonth > 31 || dayOfMonth < 1) {
                      View.display("Day of month must be between 1 and 31! Try again.");
                      continue;
                    }

                    daysOfMonth.add(dayOfMonth);

                    View.display("Do you want to add another day of the month (Y/N): ");
                    oneMore = scanner.nextLine();

                    if (!oneMore.equalsIgnoreCase("Y")) keep = false;
                  } catch (InputMismatchException e) {
                    View.display("Invalid day of the month! Try again.");
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

                String oneMore;
                boolean keep = true;

                do {
                  try {
                    View.display("Add one specific date of the year (MM-dd): ");
                    monthDayStr = scanner.nextLine();
                    monthDay = MonthDay.parse(monthDayStr.trim(), formatter);

                    daysOfYear.add(monthDay);

                    View.display("Do you want to add another date of the year (Y/N): ");
                    oneMore = scanner.nextLine();

                    if (!oneMore.equalsIgnoreCase("Y")) keep = false;
                  } catch (DateTimeParseException e) {
                    View.display("Invalid date of the year! Try again.");
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
              View.display(
                  "Are you sure you want to "
                      + varString
                      + " \""
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

  public void searchOneTask() {
    searchAllPendingTasks();
    View.display("Insert task's id: ");
    try {
      Long taskId = scanner.nextLong();
      Optional<Task> optTask = crudService.getTaskById(taskId);
      if (optTask.isPresent()) {
        View.displayOneTask(optTask.get());
      } else {
        View.display("The task's id doesn't exist!");
      }
    } catch (InputMismatchException e) {
      View.display("Not a valid task id.");
    }
  }
}
