package models;

import java.time.LocalTime;
import java.util.Set;

public class DailyRepeatOnConfig extends RepeatOnConfig {
  Set<LocalTime> hours;
  @Override
  public void accept(IRepeatOnConfigVisitor visitor) {
    visitor.visit(this);
  }
}
