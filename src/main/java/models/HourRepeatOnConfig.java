package models;

import java.util.Set;
import lombok.Data;

@Data
public class HourRepeatOnConfig implements RepeatOnConfig {
  private Set<Integer> minutes;

  @Override
  public void accept(IRepeatOnConfigVisitor visitor) {
    visitor.visit(this);
  }
}
