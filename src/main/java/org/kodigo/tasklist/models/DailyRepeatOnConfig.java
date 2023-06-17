package org.kodigo.tasklist.models;

import java.time.LocalTime;
import java.util.Set;
import lombok.Data;
import org.kodigo.tasklist.services.IRepeatOnConfigVisitor;

@Data
public class DailyRepeatOnConfig implements RepeatOnConfig {
  private Set<LocalTime> hours;

  @Override
  public void accept(IRepeatOnConfigVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return hours.toString();
  }
}
