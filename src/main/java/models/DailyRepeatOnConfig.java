package models;

import java.time.LocalTime;
import java.util.Set;
import lombok.Data;

@Data
public class DailyRepeatOnConfig extends RepeatOnConfig {
  private Set<LocalTime> hours;

  @Override
  public void accept(IRepeatOnConfigVisitor visitor) {
    visitor.visit(this);
  }
}
