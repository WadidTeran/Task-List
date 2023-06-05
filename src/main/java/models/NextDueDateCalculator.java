package models;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;

public class NextDueDateCalculator implements IRepeatOnConfigVisitor {
  private Task task;
  @Getter private LocalDate nextDueDate;
  @Getter private LocalTime nextSpecifiedTime;

  public NextDueDateCalculator(Task task) {
    this.task = task;
  }

  @Override
  public void visit(HourRepeatOnConfig repeatOnConfig) {}

  @Override
  public void visit(DailyRepeatOnConfig repeatOnConfig) {}

  @Override
  public void visit(WeeklyRepeatOnConfig repeatOnConfig) {}

  @Override
  public void visit(MonthlyRepeatOnConfig repeatOnConfig) {}

  @Override
  public void visit(YearlyRepeatOnConfig repeatOnConfig) {}
}
