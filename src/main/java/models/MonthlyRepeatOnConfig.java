package models;

import java.util.Set;

public class MonthlyRepeatOnConfig extends RepeatOnConfig {
  Set<Integer> daysOfMonth;

  @Override
  public void accept(IRepeatOnConfigVisitor visitor) {
    visitor.visit(this);
  }
}
