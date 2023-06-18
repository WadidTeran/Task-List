package org.kodigo.tasklist.services;

import org.kodigo.tasklist.models.Category;
import org.kodigo.tasklist.models.Relevance;
import org.kodigo.tasklist.models.RepeatType;
import org.kodigo.tasklist.models.Task;
import org.kodigo.tasklist.views.View;

import java.time.DayOfWeek;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class AskForService {
  private final Map<String, RepeatType> repeatTypeMap;
  private final Object[] repeatTypeArray;
  private final Map<String, Relevance> relevanceMap;
  private final Object[] relevanceArray;
  private final Map<String, DayOfWeek> daysOfWeekMap;
  private final Object[] daysOfWeekArray;
  private final Object[] daysOfMonthArray;
  private final Object[] minutesArray;
  private final CRUDService crudService;
  private final CategoryService categoryService;
  private TaskService taskService;

  public AskForService(CRUDService crudService, CategoryService categoryService) {
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
    minutesArray = Arrays.stream(minutesOptionsArray).boxed().toArray();

    ai = new AtomicInteger(1);
    int[] daysOfMonthOptionsArray = IntStream.generate(ai::getAndIncrement).limit(31).toArray();
    daysOfMonthArray = Arrays.stream(daysOfMonthOptionsArray).boxed().toArray();
  }

  public void setTaskService(TaskService taskService) {
    this.taskService = taskService;
  }

  public Optional<Task> askForATask(String message, boolean taskStatus) {
    List<Task> tasks =
        (taskStatus == TaskService.TASK_STATUS_COMPLETED)
            ? taskService.getCompletedTasks()
            : taskService.getAllPendingTasks();

    Object[] taskArray = tasks.stream().map(Task::getName).toArray();

    String task = View.inputOptions("Task selector", message, taskArray);

    if (task == null) {
      return Optional.empty();
    } else {
      return tasks.stream().filter(t -> t.getName().equals(task)).findFirst();
    }
  }

  public Optional<Category> askForACategory() {
    Object[] categoriesArray =
        crudService.findAllCategories().stream().map(Category::getName).toArray();

    String category =
        View.inputOptions("Category selector", "Choose the category", categoriesArray);

    if (category == null) {
      return Optional.empty();
    } else {
      Category categoryObj = categoryService.getCategoryByName(category);
      return Optional.of(categoryObj);
    }
  }

  public Optional<Relevance> askForARelevance() {
    String relevance =
        View.inputOptions("Relevance selector", "Choose the relevance", relevanceArray);

    if (relevance == null) {
      return Optional.empty();
    } else {
      return Optional.of(relevanceMap.get(relevance));
    }
  }

  public Optional<RepeatType> askForARepeatType() {
    String repeatType =
        View.inputOptions("Repeat Type selector", "Choose the repeat type", repeatTypeArray);

    if (repeatType == null) {
      return Optional.empty();
    } else {
      return Optional.of(repeatTypeMap.get(repeatType));
    }
  }

  public Integer askForAMinute() {
    String minuteStr =
        View.inputOptions("Minute selector", "Choose a specific minute", minutesArray);
    return (minuteStr == null) ? null : Integer.parseInt(minuteStr);
  }

  public DayOfWeek askForADayOfWeek() {
    String dayOfWeekStr =
        View.inputOptions(
            "Day of week selector", "Choose a specific day of the week", daysOfWeekArray);
    return (dayOfWeekStr == null) ? null : daysOfWeekMap.get(dayOfWeekStr);
  }

  public Integer askForADayOfMonth() {
    String dayOfMonthStr =
        View.inputOptions("Day of month selector", "Choose a day of the month", daysOfMonthArray);
    return (dayOfMonthStr == null) ? null : Integer.parseInt(dayOfMonthStr);
  }
}
