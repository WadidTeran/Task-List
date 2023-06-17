package org.kodigo.tasklist.models;

import java.util.Set;
import lombok.Data;
import org.kodigo.tasklist.services.IRepeatOnConfigVisitor;

@Data
public class MonthlyRepeatOnConfig implements RepeatOnConfig {
  private Set<Integer> daysOfMonth;

  @Override
  public void accept(IRepeatOnConfigVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return daysOfMonth.toString();
  }
}
