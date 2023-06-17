package org.kodigo.tasklist.utils;

import org.kodigo.tasklist.models.Relevance;
import org.kodigo.tasklist.models.Task;

import java.util.List;

public class ExternalUtilityMethods {
  private ExternalUtilityMethods() {}

  public static int[] countTaskByRelevance(List<Task> tasks) {
    int[] tasksByRelevance = new int[4];
    for (Task task : tasks) {
      switch (task.getRelevance()) {
        case HIGH -> tasksByRelevance[Relevance.HIGH.ordinal()]++;
        case MEDIUM -> tasksByRelevance[Relevance.MEDIUM.ordinal()]++;
        case LOW -> tasksByRelevance[Relevance.LOW.ordinal()]++;
        case NONE -> tasksByRelevance[Relevance.NONE.ordinal()]++;
      }
    }
    return tasksByRelevance;
  }

}
