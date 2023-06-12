package views;

import static views.ExternalViewMethods.*;

import java.util.Formatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import models.Category;
import models.Relevance;
import models.RepeatTaskConfig;
import models.Task;

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

  public static String input(String message) {
    return JOptionPane.showInputDialog(message.toUpperCase());
  }

  public static String inputOptions(String title, String message, Object[] optionsArray) {
    return (String)
        JOptionPane.showInputDialog(
            null,
            message.toUpperCase(),
            title.toUpperCase(),
            JOptionPane.INFORMATION_MESSAGE,
            null,
            optionsArray,
            optionsArray[0]);
  }

  public static boolean confirm(String message) {
    return JOptionPane.showConfirmDialog(
            null, message.toUpperCase(), "CONFIRMATION", JOptionPane.YES_NO_OPTION)
        == 0;
  }

  public static void displayOneTask(Task task) {
    String formatSpaces = "%25s %20s %20s %20s %30s %20s %30s %42s %n";

    putTitle("TASK: " + task.getName().toUpperCase());
    System.out.printf(
        formatSpaces,
        NAME,
        "COMPLETED DATE",
        DUEDATE,
        SPECTIME,
        DESCRIP,
        RELEVANCE,
        CATEGORY,
        "REPETITIVE CONFIG");
    lines();
    System.out.printf(
        formatSpaces,
        cutString(task.getName()),
        task.getCompletedDate(),
        task.getDueDate(),
        task.getSpecifiedTime(),
        cutString(task.getDescription()),
        task.getRelevance(),
        (task.getCategory() != null ? cutString(task.getCategory().getName()) : "N/A"),
        displayRepeatingConfig(task.getRepeatingConfig(), "%188s"));
    lines();
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
          (task.getCategory() != null ? cutString(task.getCategory().getName()) : "N/A"));
    }

    lines();
  }

  public static void displayCompletedTasks(List<Task> tasks) {
    String formatSpaces = "%25s %20s %20s %30s %15s %30s%n";
    putTitle("COMPLETED TASKS");
    System.out.printf(formatSpaces, NAME, "COMPLETED DATE", SPECTIME, DESCRIP, RELEVANCE, CATEGORY);
    lines();
    // iterates over the list
    for (Task task : tasks) {
      System.out.format(
          formatSpaces,
          cutString(task.getName()),
          task.getCompletedDate(),
          task.getSpecifiedTime(),
          cutString(task.getDescription()),
          task.getRelevance(),
          (task.getCategory() != null ? cutString(task.getCategory().getName()) : "N/A"));
    }

    lines();
  }

  public static void displayTasksByCategory(List<Task> tasks, Category category) {
    putTitle("TASKS BY CATEGORY " + category.getName().toUpperCase());
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
    putTitle("TASKS BY RELEVANCE " + relevance.name());
    System.out.printf(FORMAT, NAME, DUEDATE, SPECTIME, DESCRIP, CATEGORY);
    lines();

    for (Task task : tasks) {
      System.out.printf(
          FORMAT,
          cutString(task.getName()),
          task.getDueDate(),
          task.getSpecifiedTime(),
          cutString(task.getDescription()),
          (task.getCategory() != null ? cutString(task.getCategory().getName()) : "N/A"));
    }
    lines();
  }

  public static void displayCategories(List<Category> categories) {
    shortLines();
    System.out.printf("%30s", "CATEGORIES \n");
    shortLines();
    System.out.printf("%25s %n", NAME);
    shortLines();
    for (Category category : categories) {
      System.out.printf("%25s %n", cutString(category.getName()));
    }
    shortLines();
  }

  public static String displayRepeatingConfig(
      RepeatTaskConfig repeatTaskConfig, String firstSpace) {
    String result = "N/A";
    if (repeatTaskConfig != null) {
      try {
        try (Formatter ftmHead = new Formatter()) {
          try (Formatter ftmBody = new Formatter()) {
            ftmHead.format(
                "%15s %15s %15s %22s %n", "| TYPE", "INTERVAL", "LOCALDATE", "REPEAT CONFIG |");
            ftmBody.format(
                firstSpace + " %12s %17s %20s",
                repeatTaskConfig.getRepeatType(),
                repeatTaskConfig.getRepeatInterval(),
                repeatTaskConfig.getRepeatEndsAt(),
                getNameClass(repeatTaskConfig.getRepeatOn()));
            result = ftmHead + "\n" + ftmBody;
          }
        }
      } catch (Exception ex) {
        Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return result;
  }

  private static void displayTasks(List<Task> tasks) {
    String headFormat = "%25s %20s %18s %30s %12s %30s %n";
    System.out.printf(headFormat, NAME, DUEDATE, SPECTIME, DESCRIP, RELEVANCE, CATEGORY);
    lines();
    // iterates over the list
    for (Task task : tasks) {
      System.out.format(
          headFormat,
          cutString(task.getName()),
          task.getDueDate(),
          task.getSpecifiedTime(),
          cutString(task.getDescription()),
          task.getRelevance(),
          (task.getCategory() != null ? cutString(task.getCategory().getName()) : "N/A"));
    }

    lines();
  }
}
