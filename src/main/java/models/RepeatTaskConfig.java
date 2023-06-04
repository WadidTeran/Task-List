package models;

import java.time.LocalDate;
import lombok.Data;
import lombok.Setter;

@Data
public class RepeatTaskConfig {
  private RepeatType repeatType;
  private int repeatInterval;
  private LocalDate repeatEndsAt;
  private RepeatOnConfig repeatOn
}
