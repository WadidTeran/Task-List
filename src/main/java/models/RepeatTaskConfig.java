package models;

import java.time.LocalDate;
import lombok.Data;

@Data
public class RepeatTaskConfig {
  private RepeatType repeatType;
  private Integer repeatInterval;
  private LocalDate repeatEndsAt;
  private RepeatOnConfig repeatOn;
}
