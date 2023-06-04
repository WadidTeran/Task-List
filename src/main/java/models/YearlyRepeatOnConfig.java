package models;

import java.time.MonthDay;
import java.util.Set;
import lombok.Data;

@Data
public class YearlyRepeatOnConfig extends RepeatOnConfig {
  private Set<MonthDay> daysOfYear;

  @Override
  public void accept(IRepeatOnConfigVisitor visitor) {
    visitor.visit(this);
  }
}
