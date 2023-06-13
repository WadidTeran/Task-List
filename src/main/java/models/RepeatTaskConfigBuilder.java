package models;

import java.time.LocalDate;

public class RepeatTaskConfigBuilder {
  private RepeatTaskConfig repeatTaskConfig;

  public RepeatTaskConfigBuilder() {
    repeatTaskConfig = new RepeatTaskConfig();
  }

  public RepeatTaskConfigBuilder(RepeatTaskConfig repeatTaskConfig) {
    this.repeatTaskConfig = repeatTaskConfig;
  }

  public RepeatTaskConfigBuilder setRepeatType(RepeatType repeatType) {
    repeatTaskConfig.setRepeatType(repeatType);
    return this;
  }

  public RepeatTaskConfigBuilder setRepeatEndsAt(LocalDate repeatEndsAt) {
    repeatTaskConfig.setRepeatEndsAt(repeatEndsAt);
    return this;
  }

  public RepeatTaskConfigBuilder setRepeatInterval(Integer repeatInterval) {
    repeatTaskConfig.setRepeatInterval(repeatInterval);
    return this;
  }

  public RepeatTaskConfigBuilder setRepeatOnConfig(RepeatOnConfig repeatOn) {
    repeatTaskConfig.setRepeatOn(repeatOn);
    return this;
  }

  public void reset() {
    repeatTaskConfig = new RepeatTaskConfig();
  }

  public boolean isValidToBuild() {
    return repeatTaskConfig.getRepeatOn() != null
        && repeatTaskConfig.getRepeatType() != null
        && repeatTaskConfig.getRepeatInterval() != null;
  }

  public RepeatTaskConfig build() {
    return repeatTaskConfig;
  }
}
