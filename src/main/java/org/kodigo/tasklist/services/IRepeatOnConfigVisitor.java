package org.kodigo.tasklist.services;

import org.kodigo.tasklist.models.HourRepeatOnConfig;
import org.kodigo.tasklist.models.DailyRepeatOnConfig;
import org.kodigo.tasklist.models.WeeklyRepeatOnConfig;
import org.kodigo.tasklist.models.MonthlyRepeatOnConfig;
import org.kodigo.tasklist.models.YearlyRepeatOnConfig;

public interface IRepeatOnConfigVisitor {
  void visit(HourRepeatOnConfig repeatOnConfig);

  void visit(DailyRepeatOnConfig repeatOnConfig);

  void visit(WeeklyRepeatOnConfig repeatOnConfig);

  void visit(MonthlyRepeatOnConfig repeatOnConfig);

  void visit(YearlyRepeatOnConfig repeatOnConfig);
}
