package models;

import java.time.LocalDate;
import lombok.Data;

@Data
public class RepeatTaskConfig {
  private RepeatType repeatType;
  private LocalDate repeatEndsAt;
  private Integer repeatInterval;
  private RepeatOnConfig repeatOn;
}
