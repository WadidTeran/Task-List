package models;

import java.time.DayOfWeek;
import java.util.Set;
import lombok.Data;

@Data
public class WeeklyRepeatOnConfig extends RepeatOnConfig {
  private Set<DayOfWeek> daysOfWeek;

  @Override
  public void accept(IRepeatOnConfigVisitor visitor) {
    visitor.visit(this);
  }
}
