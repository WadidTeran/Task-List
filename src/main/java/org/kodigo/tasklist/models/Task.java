package org.kodigo.tasklist.models;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

@Data
public class Task {
  private Long taskId;
  private User user;
  private String name;

  @Getter(AccessLevel.NONE)
  private Boolean completed;

  private LocalDate completedDate;
  private LocalDate dueDate;
  private LocalTime specifiedTime;
  private String description;
  private Relevance relevance;
  private Category category;
  private RepeatTaskConfig repeatingConfig;

  public Boolean isCompleted() {
    return completed;
  }

  public boolean isRepetitive() {
    return repeatingConfig != null;
  }
}
