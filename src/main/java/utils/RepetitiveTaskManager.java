package utils;

import models.IRepeatOnConfigVisitor;
import models.NextDueDateCalculator;
import models.Task;
import models.TaskBuilder;
import services.CRUDServiceImpl;

public class RepetitiveTaskManager {
  private RepetitiveTaskManager() {}

  public static void createNewRepeatedTask(Task task, CRUDServiceImpl crudService) {
    TaskBuilder taskBuilder = TaskBuilder.taskBuilderWithClonedTask(task);
    NextDueDateCalculator nextDueDateCalculator = new NextDueDateCalculator(task);

    makeVisitorVisit(task, nextDueDateCalculator);

    taskBuilder.setDueDate(nextDueDateCalculator.getNextDueDate());
    taskBuilder.setSpecifiedTime(nextDueDateCalculator.getNextSpecifiedTime());

    Task repeatedTask = taskBuilder.build();

    crudService.saveTask(repeatedTask);
  }

  private static void makeVisitorVisit(Task task, NextDueDateCalculator visitor) {
    task.getRepeatingConfig().getRepeatOn().accept(visitor);
  }
}
