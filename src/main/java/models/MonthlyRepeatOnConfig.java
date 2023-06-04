package models;

import java.util.Set;
import lombok.Data;

@Data
public class MonthlyRepeatOnConfig extends RepeatOnConfig {
  private Set<Integer> daysOfMonth;

  @Override
  public void accept(IRepeatOnConfigVisitor visitor) {
    visitor.visit(this);
  }
}
