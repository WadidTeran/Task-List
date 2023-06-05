package models;

public interface IRepeatOnConfigVisitor {
  void visit(HourRepeatOnConfig repeatOnConfig);

  void visit(DailyRepeatOnConfig repeatOnConfig);

  void visit(WeeklyRepeatOnConfig repeatOnConfig);

  void visit(MonthlyRepeatOnConfig repeatOnConfig);

  void visit(YearlyRepeatOnConfig repeatOnConfig);
}
