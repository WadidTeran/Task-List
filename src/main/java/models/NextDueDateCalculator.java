package models;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
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
  public void visit(WeeklyRepeatOnConfig repeatOnConfig) {
    Set<DayOfWeek> daysOfWeek = repeatOnConfig.getDaysOfWeek();

    DayOfWeek oldDayOfWeek = oldDueDate.getDayOfWeek();

    if (oldDayOfWeek.equals(daysOfWeek.stream().max(Comparator.naturalOrder()).orElseThrow())) {
      nextDueDate =
          oldDueDate
              .with(daysOfWeek.stream().min(Comparator.naturalOrder()).orElseThrow())
              .plusWeeks(repeatInterval);
    } else {
      nextDueDate =
          oldDueDate.with(
              daysOfWeek.stream()
                  .filter(dow -> dow.compareTo(oldDayOfWeek) > 0)
                  .findFirst()
                  .orElseThrow());
    }
  }

  @Override
  public void visit(MonthlyRepeatOnConfig repeatOnConfig) {
    Set<Integer> daysOfMonth = repeatOnConfig.getDaysOfMonth();

    int oldDayOfMonth = oldDueDate.getDayOfMonth();

    if (oldDayOfMonth == daysOfMonth.stream().reduce(Math::max).orElseThrow()) {
      nextDueDate =
          oldDueDate
              .plusMonths(repeatInterval)
              .withDayOfMonth(daysOfMonth.stream().reduce(Math::min).orElseThrow());
    } else {
      nextDueDate =
          oldDueDate.withDayOfMonth(
              daysOfMonth.stream().filter(x -> x > oldDayOfMonth).findFirst().orElseThrow());
    }
  }

  @Override
  public void visit(YearlyRepeatOnConfig repeatOnConfig) {
    Set<MonthDay> daysOfYear = repeatOnConfig.getDaysOfYear();

    MonthDay oldMonthDay = MonthDay.of(oldDueDate.getMonth(), oldDueDate.getDayOfMonth());

    if (oldMonthDay == daysOfYear.stream().max(Comparator.naturalOrder()).orElseThrow()) {
      MonthDay firstMonthDay = daysOfYear.stream().min(Comparator.naturalOrder()).orElseThrow();
      nextDueDate =
          oldDueDate
              .plusYears(repeatInterval)
              .withMonth(firstMonthDay.getMonthValue())
              .withDayOfMonth(firstMonthDay.getDayOfMonth());
    } else {
      MonthDay nextMonthDay =
          daysOfYear.stream().filter(x -> x.compareTo(oldMonthDay) > 0).findFirst().orElseThrow();
      nextDueDate =
          oldDueDate
              .withMonth(nextMonthDay.getMonthValue())
              .withDayOfMonth(nextMonthDay.getDayOfMonth());
    }
  }
}
