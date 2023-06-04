package models;

import java.time.MonthDay;
import java.util.Set;

public class YearlyRepeatOnConfig extends RepeatOnConfig {
  Set<MonthDay> daysOfYear;
  @Override
  public void accept(IRepeatOnConfigVisitor visitor) {
    visitor.visit(this);
  }
}
