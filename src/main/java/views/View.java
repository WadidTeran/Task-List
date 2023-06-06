package views;

import models.Category;
import models.Relevance;
import models.Task;

import java.util.List;

public class View {
  private static final String FORMAT = "%25s %18s %20s %28s %30s %n";
  private static final String DUEDATE = "DUE DATE";
  private static final String NAME = "NAME";
  private static final String SPECTIME = "SPECIFIED TIME";
  private static final String DESCRIP = "DESCRIPTION";
  private static final String CATEGORY = "CATEGORY";
  private static final String RELEVANCE = "RELEVANCE";

  private View() {}

  public static void display(String string) {
    System.out.println(string);
  }

  public static void displayPendingTasksForToday(List<Task> tasks) {
    putTitle("PENDING TASKS FOR TODAY");
    displayTasks(tasks);
  }

  public static void displayFuturePendingTasks(List<Task> tasks) {
    putTitle("FUTURE PENDING TASKS");
    displayTasks(tasks);
  }

  public static void displayPastPendingTasks(List<Task> tasks) {
    putTitle("PAST PENDING TASKS");
    displayTasks(tasks);
  }

  public static void displayAllPendingTasks(List<Task> tasks) {
    putTitle("PENDING TASKS");
    final String HEAD = "%15s %28s %20s %18s %30s %12s %30s %n";
    lines();
    System.out.printf(HEAD, "TASK ID", NAME, DUEDATE, SPECTIME, DESCRIP, RELEVANCE, CATEGORY);
    lines();
    // iterates over the list
    for (Task task : tasks) {
      System.out.format(
          HEAD,
          task.getTaskId(),
          cutString(task.getName()),
          task.getDueDate(),
          task.getSpecifiedTime(),
          cutString(task.getDescription()),
          task.getRelevance(),
          cutString(task.getCategory().getName()));
    }

    lines();
  }

  public static void displayCompletedTasks(List<Task> tasks) {
    String format = "%25s %20s %20s %30s %15s %30s%n";
    putTitle("COMPLETED TASKS");
    System.out.printf(
        format, NAME, "COMPLETED DATE", SPECTIME, DESCRIP, RELEVANCE, CATEGORY);
    lines();
    // iterates over the list
    for (Task task : tasks) {
      System.out.format(
          format,
          cutString(task.getName()),
          task.getCompletedDate(),
          task.getSpecifiedTime(),
          cutString(task.getDescription()),
          task.getRelevance(),
          cutString(task.getCategory().getName()));
    }

    lines();
  }

  public static void displayTasksByCategory(List<Task> tasks, Category category) {
    putTitle("TASKS BY CATEGORY",category.getName().toUpperCase());
    System.out.printf(FORMAT, NAME, DUEDATE, SPECTIME, DESCRIP, RELEVANCE);
    lines();
    for (Task task : tasks) {
      System.out.printf(
          FORMAT,
          cutString(task.getName()),
          task.getDueDate(),
          task.getSpecifiedTime(),
          cutString(task.getDescription()),
          task.getRelevance());
    }
    lines();
  }

  public static void displayTasksByRelevance(List<Task> tasks, Relevance relevance) {
    putTitle("TASKS BY RELEVANCE",relevance.name());
    System.out.printf(FORMAT, NAME, DUEDATE, SPECTIME, DESCRIP, CATEGORY);
    lines();

    for (Task task : tasks) {
      System.out.printf(
          FORMAT,
          cutString(task.getName()),
          task.getDueDate(),
          task.getSpecifiedTime(),
          cutString(task.getDescription()),
          cutString(task.getCategory().getName()));
    }
    lines();
  }

  public static void displayCategories(List<Category> categories) {
    final String SEPARATOR = "-----------------------------------------------";
    System.out.println(SEPARATOR);
    System.out.printf("%30s", "CATEGORIES \n");
    System.out.println(SEPARATOR);
    System.out.printf("%25s %n", NAME);
    System.out.println(SEPARATOR);
    for (Category category : categories) {
      System.out.printf("%25s %n", cutString(category.getName()));
    }
    System.out.println(SEPARATOR);
  }

  private static String cutString(String text) {
    return (text.length() > 24 ? text.substring(0, 22) + "..." : text);
  }

  private static void putTitle(String title,String plus){
    lines();
    System.out.printf("%95s", title + " " + plus +"\n");
    lines();
  }
  private static void putTitle(String title){
    lines();
    System.out.printf("%95s", title +"\n");
    lines();
  }

  private static void displayTasks(List<Task> tasks) {
    final String HEAD = "%25s %20s %18s %30s %12s %30s %n";
    System.out.printf(HEAD, NAME, DUEDATE, SPECTIME, DESCRIP, RELEVANCE, CATEGORY);
    lines();
    // iterates over the list
    for (Task task : tasks) {
      System.out.format(
          HEAD,
          cutString(task.getName()),
          task.getDueDate(),
          task.getSpecifiedTime(),
          cutString(task.getDescription()),
          task.getRelevance(),
          cutString(task.getCategory().getName()));
    }

    lines();
  }

  private static void lines() {
    System.out.println(
        "-------------------------------------------------------------------------------------------------------------------------------------------------------------------");
  }
}
