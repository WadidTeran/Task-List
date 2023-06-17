package org.kodigo.tasklist.models;

import java.time.DayOfWeek;
import java.util.Set;
import lombok.Data;
import org.kodigo.tasklist.services.IRepeatOnConfigVisitor;

@Data
public class WeeklyRepeatOnConfig implements RepeatOnConfig {
  private Set<DayOfWeek> daysOfWeek;

  @Override
  public void accept(IRepeatOnConfigVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return daysOfWeek.toString();
  }
}
