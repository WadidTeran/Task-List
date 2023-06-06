package utils;

import java.time.LocalDate;
import java.time.LocalTime;
import models.NextDueDateCalculator;
import models.Task;
import models.TaskBuilder;
import services.CRUDServiceImpl;

public class RepetitiveTaskManager {
  private RepetitiveTaskManager() {}

  public static void manageRepetitiveTask(Task task, CRUDServiceImpl crudService) {
    TaskBuilder taskBuilder = TaskBuilder.taskBuilderWithClonedTask(task);
    NextDueDateCalculator nextDueDateCalculator = new NextDueDateCalculator(task);

    makeVisitorVisit(task, nextDueDateCalculator);

    LocalDate nextDueDate = nextDueDateCalculator.getNextDueDate();

    if (task.getRepeatingConfig().getRepeatEndsAt() == null
        || nextDueDate.isBefore(task.getRepeatingConfig().getRepeatEndsAt())) {
      LocalTime nextSpecifiedTime = nextDueDateCalculator.getNextSpecifiedTime();

      taskBuilder.setDueDate(nextDueDate);
      taskBuilder.setSpecifiedTime(nextSpecifiedTime);

      Task repeatedTask = taskBuilder.build();

      crudService.saveTask(repeatedTask);
    }
  }

  private static void makeVisitorVisit(Task task, NextDueDateCalculator visitor) {
    task.getRepeatingConfig().getRepeatOn().accept(visitor);
  }
}
