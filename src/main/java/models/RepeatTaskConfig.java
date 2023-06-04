package models;

import java.time.LocalDate;
import lombok.Data;
import lombok.Setter;

@Data
public class RepeatTaskConfig {
  @Setter private RepeatType repeatType;
  @Setter private int repeatInterval;
  @Setter private LocalDate repeatEndsAt;
  @Setter private RepeatOnConfig repeatOn;
}
