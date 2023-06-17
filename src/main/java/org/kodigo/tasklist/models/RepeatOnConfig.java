package org.kodigo.tasklist.models;

import org.kodigo.tasklist.services.IRepeatOnConfigVisitor;

public interface RepeatOnConfig {

  void accept(IRepeatOnConfigVisitor visitor);
}
