package views;

import java.util.List;
import models.Category;
import models.Relevance;
import models.Task;

public class View {
  private View() {}

  public static void display(String string) {}

  public static void displayTasksByCategory(List<Task> tasks, Category categoryObj) {}

  public static void displayTasksByRelevance(List<Task> relevanceTasks, Relevance relevance) {}

  public static void displayCategories(List<Category> categories) {}

  public static void displayCompletedTasks(List<Task> completedTasks) {}
}
