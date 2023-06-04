package models;

import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;

@Data
public class RepeatTaskConfig {
  private RepeatType repeatType;
  private int repeatInterval;
  private LocalDate repeatEndsAt;
  private RepeatOnConfig repeatOn;
}
