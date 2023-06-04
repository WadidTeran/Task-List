package models;

import java.time.DayOfWeek;
import java.util.Set;

public class WeeklyRepeatOnConfig extends RepeatOnConfig {
  Set<DayOfWeek> daysOfWeek;
  @Override
  public void accept(IRepeatOnConfigVisitor visitor) {
    visitor.visit(this);
  }
}
