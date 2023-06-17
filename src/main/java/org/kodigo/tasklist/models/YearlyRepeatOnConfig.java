package org.kodigo.tasklist.models;

import java.time.MonthDay;
import java.util.Set;
import lombok.Data;
import org.kodigo.tasklist.services.IRepeatOnConfigVisitor;

@Data
public class YearlyRepeatOnConfig implements RepeatOnConfig {
  private Set<MonthDay> daysOfYear;

  @Override
  public void accept(IRepeatOnConfigVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return daysOfYear.toString();
  }
}
