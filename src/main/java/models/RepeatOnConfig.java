package models;

import services.IRepeatOnConfigVisitor;

public interface RepeatOnConfig {

  void accept(IRepeatOnConfigVisitor visitor);
}
