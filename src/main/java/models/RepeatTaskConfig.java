package models;

import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;

@Data
public class RepeatTaskConfig {
  @Setter
  private RepeatType repeatType;
  @Setter
  private int repeatInterval;
  @Setter
  private LocalDate repeatEndsAt;
  @Setter
  private RepeatOnConfig repeatOn;
}
