package org.kodigo.tasklist.views;

import java.util.List;
import javax.swing.JOptionPane;

import org.kodigo.tasklist.models.Category;
import org.kodigo.tasklist.models.Relevance;
import org.kodigo.tasklist.models.RepeatTaskConfig;
import org.kodigo.tasklist.models.Task;
import org.kodigo.tasklist.utils.Warnings;

public class View {
  private static final String FORMAT = "%25s %18s %20s %28s %30s %n";
  private static final String DUEDATE = "DUE DATE";
  private static final String NAME = "NAME";
  private static final String SPECTIME = "SPECIFIED TIME";
  private static final String DESCRIP = "DESCRIPTION";
  private static final String CATEGORY = "CATEGORY";
  private static final String RELEVANCE = "RELEVANCE";

  private View() {}

  public static void message(String message) {
    JOptionPane.showMessageDialog(null, message.toUpperCase());
  }

  public static void message(Warnings warning) {
    JOptionPane.showMessageDialog(null, warning.toString().toUpperCase());
  }

  public static String input(String message) {
    return JOptionPane.showInputDialog(message.toUpperCase());
  }

  public static String inputOptions(String title, String message, Object[] optionsArray) {
    Object obj =
        JOptionPane.showInputDialog(
            null,
            message.toUpperCase(),
            title.toUpperCase(),
            JOptionPane.INFORMATION_MESSAGE,
            null,
            optionsArray,
            optionsArray[0]);
    return (obj == null ? null : obj.toString());
  }

  public static boolean confirm(String message) {
    return JOptionPane.showConfirmDialog(
            null, message.toUpperCase(), "CONFIRMATION", JOptionPane.YES_NO_OPTION)
        == 0;
  }

  public static void displayOneTask(Task task) {
    String title = "TASK: " + task.getName().toUpperCase();

    System.out.println(String.valueOf('-').repeat(115));
    System.out.printf("%67s", title);
    System.out.println("\n" + String.valueOf('-').repeat(115));

    System.out.println("Name: " + task.getName());
    System.out.println(String.valueOf('-').repeat(115));
    System.out.println("Due Date: " + task.getDueDate());
    System.out.println(String.valueOf('-').repeat(115));
    System.out.println("Specified Time: " + task.getSpecifiedTime());
    System.out.println(String.valueOf('-').repeat(115));
    if (task.getDescription() != null
        && !task.getDescription().isEmpty()
        && !task.getDescription().isBlank()) {
      ExternalViewMethods.displayMultiline("Description: ", task.getDescription());
      System.out.println(String.valueOf('-').repeat(115));
    }
    System.out.println("Relevance: " + task.getRelevance());
    System.out.println(String.valueOf('-').repeat(115));
    if (task.getCategory() != null) {
      System.out.println(
          "Category: " + ExternalViewMethods.cutString(task.getCategory().getName()));
      System.out.println(String.valueOf('-').repeat(115));
    }
    System.out.println("Repetitive Config: \n" + displayRepeatingConfig(task.getRepeatingConfig()));
    System.out.println(String.valueOf('-').repeat(115));
  }

  public static void displayPendingTasksForToday(List<Task> tasks) {
    ExternalViewMethods.putTitle("PENDING TASKS FOR TODAY");
    displayTasks(tasks);
  }

  public static void displayFuturePendingTasks(List<Task> tasks) {
    ExternalViewMethods.putTitle("FUTURE PENDING TASKS");
    displayTasks(tasks);
  }

  public static void displayPastPendingTasks(List<Task> tasks) {
    ExternalViewMethods.putTitle("PAST PENDING TASKS");
    displayTasks(tasks);
  }

  public static void displayAllPendingTasks(List<Task> tasks) {
    ExternalViewMethods.putTitle("PENDING TASKS");
    final String HEAD = "%15s %28s %20s %18s %30s %12s %30s %n";
    System.out.printf(HEAD, "TASK ID", NAME, DUEDATE, SPECTIME, DESCRIP, RELEVANCE, CATEGORY);
    System.out.println(String.valueOf('-').repeat(170));
    // iterates over the list
    for (Task task : tasks) {
      System.out.format(
          HEAD,
          task.getTaskId(),
          ExternalViewMethods.cutString(task.getName()),
          task.getDueDate(),
          task.getSpecifiedTime(),
          ExternalViewMethods.cutString(task.getDescription()),
          task.getRelevance(),
          (task.getCategory() != null
              ? ExternalViewMethods.cutString(task.getCategory().getName())
              : "N/A"));
    }
    System.out.println(String.valueOf('-').repeat(170));
  }

  public static void displayCompletedTasks(List<Task> tasks) {
    String formatSpaces = "%25s %20s %20s %30s %15s %30s%n";
    ExternalViewMethods.putTitle("COMPLETED TASKS");
    System.out.printf(formatSpaces, NAME, "COMPLETED DATE", DUEDATE, DESCRIP, RELEVANCE, CATEGORY);
    System.out.println(String.valueOf('-').repeat(170));
    // iterates over the list
    for (Task task : tasks) {
      System.out.format(
          formatSpaces,
          ExternalViewMethods.cutString(task.getName()),
          task.getCompletedDate(),
          task.getDueDate(),
          ExternalViewMethods.cutString(task.getDescription()),
          task.getRelevance(),
          (task.getCategory() != null
              ? ExternalViewMethods.cutString(task.getCategory().getName())
              : "N/A"));
    }
    System.out.println(String.valueOf('-').repeat(170));
  }

  public static void displayTasksByCategory(List<Task> tasks, Category category) {
    ExternalViewMethods.putTitle("TASKS BY CATEGORY " + category.getName().toUpperCase());
    System.out.printf(FORMAT, NAME, DUEDATE, SPECTIME, DESCRIP, RELEVANCE);
    System.out.println(String.valueOf('-').repeat(170));
    for (Task task : tasks) {
      System.out.printf(
          FORMAT,
          ExternalViewMethods.cutString(task.getName()),
          task.getDueDate(),
          task.getSpecifiedTime(),
          ExternalViewMethods.cutString(task.getDescription()),
          task.getRelevance());
    }
    System.out.println(String.valueOf('-').repeat(170));
  }

  public static void displayTasksByRelevance(List<Task> tasks, Relevance relevance) {
    ExternalViewMethods.putTitle("TASKS BY RELEVANCE " + relevance.name());
    System.out.printf(FORMAT, NAME, DUEDATE, SPECTIME, DESCRIP, CATEGORY);
    System.out.println(String.valueOf('-').repeat(170));
    for (Task task : tasks) {
      System.out.printf(
          FORMAT,
          ExternalViewMethods.cutString(task.getName()),
          task.getDueDate(),
          task.getSpecifiedTime(),
          ExternalViewMethods.cutString(task.getDescription()),
          (task.getCategory() != null
              ? ExternalViewMethods.cutString(task.getCategory().getName())
              : "N/A"));
    }
    System.out.println(String.valueOf('-').repeat(170));
  }

  public static void displayCategories(List<Category> categories) {
    System.out.println(String.valueOf('-').repeat(46));
    System.out.printf("%30s", "CATEGORIES \n");
    System.out.println(String.valueOf('-').repeat(46));
    System.out.printf("%25s %n", NAME);
    System.out.println(String.valueOf('-').repeat(46));
    for (Category category : categories) {
      System.out.printf("%25s %n", ExternalViewMethods.cutString(category.getName()));
    }
    System.out.println(String.valueOf('-').repeat(46));
  }

  public static String displayRepeatingConfig(RepeatTaskConfig repeatTaskConfig) {
    if (repeatTaskConfig != null) {
      return "    TYPE: "
          + repeatTaskConfig.getRepeatType().name()
          + "\n"
          + "    INTERVAL: "
          + repeatTaskConfig.getRepeatInterval()
          + "\n"
          + "    ENDS AT: "
          + ((repeatTaskConfig.getRepeatEndsAt() != null)
              ? repeatTaskConfig.getRepeatEndsAt()
              : "Endlessly")
          + "\n"
          + "    REPEAT CONFIG: "
          + repeatTaskConfig.getRepeatOn();
    } else {
      return "N/A";
    }
  }

  private static void displayTasks(List<Task> tasks) {
    String headFormat = "%25s %20s %18s %30s %12s %30s %n";
    System.out.printf(headFormat, NAME, DUEDATE, SPECTIME, DESCRIP, RELEVANCE, CATEGORY);
    System.out.println(String.valueOf('-').repeat(170));
    // iterates over the list
    for (Task task : tasks) {
      System.out.format(
          headFormat,
          ExternalViewMethods.cutString(task.getName()),
          task.getDueDate(),
          task.getSpecifiedTime(),
          ExternalViewMethods.cutString(task.getDescription()),
          task.getRelevance(),
          (task.getCategory() != null
              ? ExternalViewMethods.cutString(task.getCategory().getName())
              : "N/A"));
    }
    System.out.println(String.valueOf('-').repeat(170));
  }
}
