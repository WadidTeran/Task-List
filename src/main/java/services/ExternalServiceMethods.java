package services;

import models.Task;
import utils.RangeDates;

import java.util.List;

public class ExternalServiceMethods {

  private ExternalServiceMethods() {}

  public static List<Task> filterCompletedTasksByDate(List<Task> tasks, RangeDates range) {
    return tasks.stream()
        .filter(
            t ->
                t.getCompletedDate().isAfter(range.startDate())
                        && t.getCompletedDate().isBefore(range.endDate())
                    || t.getCompletedDate().equals(range.startDate())
                    || t.getCompletedDate().equals(range.endDate()))
        .toList();
  }
}
