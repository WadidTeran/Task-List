package org.kodigo.tasklist.models;

public enum RepeatType {
  HOUR("hour"),
  DAILY("days"),
  WEEKLY("weeks"),
  MONTHLY("months"),
  YEARLY("years");
  final String type;

  RepeatType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return type;
  }
}
