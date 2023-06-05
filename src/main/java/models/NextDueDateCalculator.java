package models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import lombok.Getter;

public class NextDueDateCalculator implements IRepeatOnConfigVisitor {
  private Task task;
  @Getter private LocalDate nextDueDate;
  @Getter private LocalTime nextSpecifiedTime;

  public NextDueDateCalculator(Task task) {
    this.task = task;
  }

  @Override
  public void visit(HourRepeatOnConfig repeatOnConfig) {
    Set<Integer> minutes = repeatOnConfig.getMinutes();
    LocalDate oldDueDate = task.getDueDate();
    nextDueDate = oldDueDate;
    LocalTime oldSpecifiedTime = task.getSpecifiedTime();
    Integer interval = task.getRepeatingConfig().getRepeatInterval();

    int oldMinutes = oldSpecifiedTime.getMinute();

    if (oldMinutes == minutes.stream().reduce(Math::max).orElseThrow()) {
      nextSpecifiedTime =
          oldSpecifiedTime
              .plusHours(interval)
              .withMinute(minutes.stream().reduce(Math::min).orElseThrow());
      if (nextSpecifiedTime.isBefore(oldSpecifiedTime)) nextDueDate = oldDueDate.plusDays(1);
    } else {
      nextSpecifiedTime =
          oldSpecifiedTime.withMinute(
              minutes.stream().filter(x -> x > oldMinutes).findFirst().orElseThrow());
    }
  }

  @Override
  public void visit(DailyRepeatOnConfig repeatOnConfig) {}

  @Override
  public void visit(WeeklyRepeatOnConfig repeatOnConfig) {}

  @Override
  public void visit(MonthlyRepeatOnConfig repeatOnConfig) {}

  @Override
  public void visit(YearlyRepeatOnConfig repeatOnConfig) {}
}
