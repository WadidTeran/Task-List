package services;

import java.time.LocalDate;
import java.time.LocalTime;

import models.Task;
import models.TaskBuilder;

public class RepetitiveTaskService {
  private RepetitiveTaskService() {}

  public static void manageRepetitiveTask(Task task, ICRUDService crudService) {
    TaskBuilder taskBuilder = TaskBuilder.taskBuilderWithClonedTask(task);
    NextDueDateCalculatorService nextDueDateCalculatorService =
        new NextDueDateCalculatorService(task);

    makeVisitorVisit(task, nextDueDateCalculatorService);

    LocalDate nextDueDate = nextDueDateCalculatorService.getNextDueDate();

    if (task.getRepeatingConfig().getRepeatEndsAt() == null
        || nextDueDate.isBefore(task.getRepeatingConfig().getRepeatEndsAt())) {
      LocalTime nextSpecifiedTime = nextDueDateCalculatorService.getNextSpecifiedTime();

      taskBuilder.setDueDate(nextDueDate);
      taskBuilder.setSpecifiedTime(nextSpecifiedTime);

      Task repeatedTask = taskBuilder.build();

      crudService.saveTask(repeatedTask);
    }
  }

  private static void makeVisitorVisit(Task task, NextDueDateCalculatorService visitor) {
    task.getRepeatingConfig().getRepeatOn().accept(visitor);
  }
}
