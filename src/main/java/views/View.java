package views;

import java.util.ArrayList;
import java.util.List;
import models.Category;
import models.Relevance;
import models.Task;

public class View {
  private View() {}

  public static void display(String string) {}

  public static void displayTasksByCategory(List<Task> tasks, Category category) {}

  public static void displayTasksByRelevance(List<Task> tasks, Relevance relevance) {}

  public static void displayCategories(List<Category> categories) {}

  public static void displayCompletedTasks(List<Task> tasks) {}

    public static void displayAllPendingTasks(List<Task> tasks) {
    }

  public static void displayPastPendingTasks(List<Task> tasks) {
  }

  public static void displayPendingTasksForToday(List<Task> tasks) {
  }

  public static void displayFuturePendingTasks(List<Task> tasks) {
  }
}
