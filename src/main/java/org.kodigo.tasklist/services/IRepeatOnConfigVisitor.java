package services;

import models.HourRepeatOnConfig;
import models.DailyRepeatOnConfig;
import models.WeeklyRepeatOnConfig;
import models.MonthlyRepeatOnConfig;
import models.YearlyRepeatOnConfig;

public interface IRepeatOnConfigVisitor {
  void visit(HourRepeatOnConfig repeatOnConfig);

  void visit(DailyRepeatOnConfig repeatOnConfig);

  void visit(WeeklyRepeatOnConfig repeatOnConfig);

  void visit(MonthlyRepeatOnConfig repeatOnConfig);

  void visit(YearlyRepeatOnConfig repeatOnConfig);
}
