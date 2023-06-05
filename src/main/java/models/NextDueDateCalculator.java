package models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Set;
import lombok.Getter;

public class NextDueDateCalculator implements IRepeatOnConfigVisitor {
  private final LocalDate oldDueDate;
  private final LocalTime oldSpecifiedTime;
  private final Integer repeatInterval;
  @Getter private LocalDate nextDueDate;
  @Getter private LocalTime nextSpecifiedTime;

  public NextDueDateCalculator(Task task) {
    oldDueDate = task.getDueDate();
    oldSpecifiedTime = task.getSpecifiedTime();
    repeatInterval = task.getRepeatingConfig().getRepeatInterval();

    nextDueDate = oldDueDate;
    nextSpecifiedTime = oldSpecifiedTime;
  }

  @Override
  public void visit(HourRepeatOnConfig repeatOnConfig) {
    Set<Integer> minutes = repeatOnConfig.getMinutes();

    int oldMinutes = oldSpecifiedTime.getMinute();

    if (oldMinutes == minutes.stream().reduce(Math::max).orElseThrow()) {
      nextSpecifiedTime =
          oldSpecifiedTime
              .plusHours(repeatInterval)
              .withMinute(minutes.stream().reduce(Math::min).orElseThrow());
      if (nextSpecifiedTime.isBefore(oldSpecifiedTime)) nextDueDate = oldDueDate.plusDays(1);
    } else {
      nextSpecifiedTime =
          oldSpecifiedTime.withMinute(
              minutes.stream().filter(x -> x > oldMinutes).findFirst().orElseThrow());
    }
  }

  @Override
  public void visit(DailyRepeatOnConfig repeatOnConfig) {
    Set<LocalTime> hours = repeatOnConfig.getHours();

    LocalTime oldHour = oldSpecifiedTime;

    if (oldHour.equals(hours.stream().max(Comparator.naturalOrder()).orElseThrow())) {
      nextDueDate = oldDueDate.plusDays(repeatInterval);
      nextSpecifiedTime = hours.iterator().next();

    } else {
      nextSpecifiedTime =
          hours.stream().filter(d -> d.compareTo(oldHour) > 0).findFirst().orElseThrow();
    }
  }

  @Override
  public void visit(WeeklyRepeatOnConfig repeatOnConfig) {}

  @Override
  public void visit(MonthlyRepeatOnConfig repeatOnConfig) {}

  @Override
  public void visit(YearlyRepeatOnConfig repeatOnConfig) {}
}
