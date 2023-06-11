package services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import models.*;
import views.View;

public class TaskCreatorModificatorService {
  private static Map<String, Integer> menuOptions;

  private TaskCreatorModificatorService() {}

  private static String switchMenuAndTitle(TaskOperationType taskOperationType) {
    menuOptions = new LinkedHashMap<>();

    if (taskOperationType == TaskOperationType.CREATION) {
      menuOptions.put("SET NAME", 1);
      menuOptions.put("SET DUE DATE", 2);
      menuOptions.put("SET A SPECIFIED TIME", 3);
      menuOptions.put("SET DESCRIPTION", 4);
      menuOptions.put("SET RELEVANCE", 5);
      menuOptions.put("SET CATEGORY", 6);
      menuOptions.put("SET REPEAT CONFIG", 7);
      menuOptions.put("CREATE", 8);
    } else if (taskOperationType == TaskOperationType.MODIFICATION) {
      menuOptions.put("CHANGE NAME", 1);
      menuOptions.put("CHANGE DUE DATE", 2);
      menuOptions.put("CHANGE SPECIFIED TIME", 3);
      menuOptions.put("CHANGE DESCRIPTION", 4);
      menuOptions.put("CHANGE RELEVANCE", 5);
      menuOptions.put("CHANGE CATEGORY", 6);
      menuOptions.put("CHANGE REPEAT CONFIG", 7);
      menuOptions.put("CONFIRM CHANGES", 8);
    }

    return (taskOperationType == TaskOperationType.CREATION)
        ? "Task creation mode"
        : "Task modification mode";
  }

  // TODO: THIS METHOD NEEDS TO BE REFACTORED
  public static void process(
      TaskBuilder taskBuilder,
      TaskOperationType taskOperationType,
      ICRUDService crudService,
      CategoryService categoryService) {

    String title = switchMenuAndTitle(taskOperationType);
    Object[] opArreglo = menuOptions.keySet().toArray();
    int opcionIndice;

    do {
      String opcion = View.inputOptions(title, "Choose an option", opArreglo);

      if (opcion == null) {
        if (View.confirm("Are you sure you want to cancel?")) break;
      } else {
        opcionIndice = menuOptions.get(opcion);

        if (opcionIndice == 1) {
          String name = View.input("Name");

          if (name.length() > TaskService.MAX_TASK_NAME_LENGTH) {
            View.message(TaskService.LONG_TASK_NAME_WARNING);
          } else if (name.isBlank() || name.isEmpty()) {
            View.message("Not a valid name!");
          } else {
            taskBuilder.setName(name);
          }
        } else if (opcionIndice == 2) {
          String dueDateStr = View.input("Due date (yyyy-MM-dd)");

          try {
            LocalDate dueDate = LocalDate.parse(dueDateStr);
            taskBuilder.setDueDate(dueDate);
          } catch (DateTimeParseException e) {
            View.message("Invalid due date format");
          }
        } else if (opcionIndice == 3) {
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
        } else if (opcionIndice == 4) {
          String description = View.input("Description");

          if (description.length() > 300) {
            View.message(TaskService.MAX_DESCRIPTION_LENGTH_WARNING);
          } else if (description.isBlank() || description.isEmpty()) {
            View.message("Not a valid description!");
          } else {
            taskBuilder.setDescription(description);
          }
        } else if (opcionIndice == 5) {
          Map<String, Relevance> relevanceMap = Relevance.getRelevanceMap();
          Object[] relevanceOptionsArray = relevanceMap.keySet().toArray();

          String relevanceStr =
              View.inputOptions(title, "Choose a level of relevance", relevanceOptionsArray);

          if (relevanceStr == null) {
            View.message("You have to choose a level of relevance!");
          } else {
            Relevance relevance = relevanceMap.get(relevanceStr);
            taskBuilder.setRelevance(relevance);
          }
        } else if (opcionIndice == 6) {
          Object[] categoriesArray =
              crudService.findAllCategories().stream().map(Category::getName).toArray();

          String category =
              View.inputOptions("Category selector", "Choose the category", categoriesArray);

          if (!categoryService.checkCategoryName(category)) {
            View.message("The category " + category + " doesn't exist.");
          } else {
            Category categoryObj = categoryService.getCategoryByName(category);
            taskBuilder.setCategory(categoryObj);
          }
        } else if (opcionIndice == 7) {
          if (taskBuilder.build().getDueDate() != null) {
            if (taskBuilder.build().getRepeatingConfig() != null
                && (View.confirm("Do you want to set this task as not repetitive?"))) {
              taskBuilder.setRepeatingConfig(null);
              continue;
            }
            RepeatTaskConfig repeatingConfig = new RepeatTaskConfig();

            Map<String, RepeatType> repeatTypeMap = new LinkedHashMap<>();
            repeatTypeMap.put("Hour", RepeatType.HOUR);
            repeatTypeMap.put("Daily", RepeatType.DAILY);
            repeatTypeMap.put("Weekly", RepeatType.WEEKLY);
            repeatTypeMap.put("Monthly", RepeatType.MONTHLY);
            repeatTypeMap.put("Yearly", RepeatType.YEARLY);

            Object[] repeatTypeOptionsArray = repeatTypeMap.keySet().toArray();

            String repeatTypeStr =
                View.inputOptions(title, "Choose a type of repetition", repeatTypeOptionsArray);

            RepeatType repeatType;

            if (repeatTypeStr == null) {
              View.message("You have to choose a type of repetition!");
              continue;
            } else {
              repeatType = repeatTypeMap.get(repeatTypeStr);

              if (repeatType.equals(RepeatType.HOUR)
                  && taskBuilder.build().getSpecifiedTime() == null) {
                View.message(
                    "To set a task as hourly repetitive you must set first a specified time for this task!");
                continue;
              }

              repeatingConfig.setRepeatType(repeatType);
            }

            if (View.confirm("Do you want to set a end date for repetitions?")) {
              String repeatEndsAtStr = View.input("Repeat ends at (yyyy-mm-dd)");

              try {
                LocalDate repeatEndsAt = LocalDate.parse(repeatEndsAtStr);
                repeatingConfig.setRepeatEndsAt(repeatEndsAt);
              } catch (DateTimeParseException e) {
                View.message("Invalid end date format.");
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
              Integer interval = Integer.parseInt(View.input("Repeat each ? " + typeStr));
              repeatingConfig.setRepeatInterval(interval);
            } catch (NumberFormatException nfe) {
              View.message("Invalid interval: interval must be an integer.");
              continue;
            }

            switch (repeatType) {
              case HOUR -> {
                HourRepeatOnConfig hourRepeatOnConfig = new HourRepeatOnConfig();
                Set<Integer> minutes = new TreeSet<>();
                int minute;

                AtomicInteger ai = new AtomicInteger(0);
                int[] minutesOptionsArray =
                    IntStream.generate(ai::getAndIncrement).limit(60).toArray();
                Object[] minuteOptionsArrayObj =
                    Arrays.stream(minutesOptionsArray).boxed().toArray();
                boolean keep = true;

                do {
                  try {
                    minute =
                        Integer.parseInt(
                            View.inputOptions(
                                title, "Choose a specific minute", minuteOptionsArrayObj));
                    minutes.add(minute);

                    if (View.confirm("Do you want to add another minute?")) keep = false;
                  } catch (NumberFormatException nfe) {
                    View.message("Invalid minute! Try again.");
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
                    hourStr = View.input("Add one specific hour (00:00 - 23:59)");
                    hour = LocalTime.parse(hourStr.trim());

                    hours.add(hour);

                    if (hours.size() == 24
                        || View.confirm("Do you want to add another hour? (24 max)")) keep = false;
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

                Map<String, DayOfWeek> daysOfWeekMap = new LinkedHashMap<>();
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
                      View.inputOptions(
                          title, "Choose a specific day of the week", daysOfWeekArray);

                  if (dayOfWeekStr == null) {
                    View.message("You have to choose a specific day of the week!");
                  } else {
                    dayOfWeek = daysOfWeekMap.get(dayOfWeekStr);
                    daysOfWeek.add(dayOfWeek);

                    if (View.confirm("Do you want to add another day of the week?")) keep = false;
                  }

                } while (keep);

                weeklyRepeatOnConfig.setDaysOfWeek(daysOfWeek);
                repeatingConfig.setRepeatOn(weeklyRepeatOnConfig);
              }
              case MONTHLY -> {
                MonthlyRepeatOnConfig monthlyRepeatOnConfig = new MonthlyRepeatOnConfig();
                Set<Integer> daysOfMonth = new TreeSet<>();
                int dayOfMonth;

                AtomicInteger ai = new AtomicInteger(1);
                int[] daysOfMonthOptionsArray =
                    IntStream.generate(ai::getAndIncrement).limit(31).toArray();
                Object[] daysOfMonthOptionsArrayStr =
                    Arrays.stream(daysOfMonthOptionsArray).boxed().toArray();
                boolean keep = true;

                do {
                  try {
                    dayOfMonth =
                        Integer.parseInt(
                            View.inputOptions(
                                title, "Choose a day of the month", daysOfMonthOptionsArrayStr));

                    daysOfMonth.add(dayOfMonth);

                    if (View.confirm("Do you want to add another day of the month?")) keep = false;
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

                boolean keep = true;

                do {
                  try {
                    monthDayStr = View.input("Add one specific date of the year (MM-dd)");
                    monthDay = MonthDay.parse(monthDayStr.trim(), formatter);

                    daysOfYear.add(monthDay);

                    if (daysOfYear.size() == 12
                        || View.confirm("Do you want to add another date of the year? (12 max)"))
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

          } else {
            View.message("You have to set a due date first!");
          }

        } else if (opcionIndice == 8) {
          if (taskBuilder.build().getName() != null) {
            String varString = (title.contains("modification")) ? "update" : "create";

            if (View.confirm(
                "Are you sure you want to "
                    + varString
                    + " \""
                    + taskBuilder.build().getName()
                    + "\"?")) {
              Task newTask = taskBuilder.build();
              crudService.saveTask(newTask);
            }
          } else {
            View.message("You have to set a name for this task!");
          }
        }
      }
    } while (true);
  }
}
