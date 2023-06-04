package models;

import java.util.Set;

public class HourRepeatOnConfig extends RepeatOnConfig {
  Set<Integer> minutes;
  @Override
  public void accept(IRepeatOnConfigVisitor visitor) {
    visitor.visit(this);
  }
}
