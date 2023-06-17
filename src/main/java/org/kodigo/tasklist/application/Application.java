package org.kodigo.tasklist.application;

import org.kodigo.tasklist.menus.AbstractMenu;
import org.kodigo.tasklist.menus.LoginMenu;
import org.kodigo.tasklist.models.*;
import org.kodigo.tasklist.repositories.CategoryRepository;
import org.kodigo.tasklist.services.CRUDService;
import org.kodigo.tasklist.services.CategoryService;
import org.kodigo.tasklist.services.TaskService;
import org.kodigo.tasklist.services.UserService;
import org.kodigo.tasklist.repositories.IRepository;
import org.kodigo.tasklist.repositories.TaskRepository;
import org.kodigo.tasklist.repositories.UserRepository;

import javax.swing.UIManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.util.Set;
import java.util.TreeSet;

public class Application {
  private static final IRepository<User> userRepository = new UserRepository();
  private static final IRepository<Task> taskRepository = new TaskRepository();
  private static final IRepository<Category> categoryRepository = new CategoryRepository();
  private static final CRUDService crudService =
      new CRUDService(userRepository, taskRepository, categoryRepository);
  private static final UserService userService = new UserService(crudService);
  private static final CategoryService categoryService = new CategoryService(crudService);
  private static final TaskService taskService = new TaskService(crudService, categoryService);

  private Application() {}

  public static void start() {
    createTestData();

    UIManager.put("OptionPane.cancelButtonText", "BACK");
    UIManager.put("OptionPane.yesButtonText", "YES");
    UIManager.put("OptionPane.noButtonText", "NO");

    AbstractMenu currentMenu = new LoginMenu(userService, taskService, categoryService);

    while (currentMenu != null) {
      currentMenu = currentMenu.showMenu();
    }
  }

  private static void createTestData() {
    User user = new User("admin", "secret");

    Category category1 = new Category("Workout", user);
    Category category2 = new Category("University", user);
    Category category3 = new Category("Day-to-day", user);
    Category category4 = new Category("Birthdays", user);

    Task task1 = new Task();
    task1.setUser(user);
    task1.setName("Sweep and mop the floor");
    task1.setDescription("");
    task1.setCompleted(false);
    task1.setCategory(category3);
    task1.setRelevance(Relevance.LOW);

    task1.setDueDate(LocalDate.now().plusDays(1));
    task1.setSpecifiedTime(LocalTime.of(16, 30));
    RepeatTaskConfig repeatingConfig = new RepeatTaskConfig();
    repeatingConfig.setRepeatType(RepeatType.DAILY);
    repeatingConfig.setRepeatInterval(2);
    DailyRepeatOnConfig dailyRepeatOnConfig = new DailyRepeatOnConfig();
    Set<LocalTime> hours = new TreeSet<>();
    hours.add(LocalTime.of(12, 0));
    hours.add(LocalTime.of(18, 0));
    dailyRepeatOnConfig.setHours(hours);
    repeatingConfig.setRepeatOn(dailyRepeatOnConfig);
    task1.setRepeatingConfig(repeatingConfig);

    Task task2 = new Task();
    task2.setUser(user);
    task2.setName("Family birthdays");
    task2.setCompleted(false);
    task2.setDescription("");
    task2.setCategory(category4);
    task2.setRelevance(Relevance.HIGH);
    task2.setDueDate(LocalDate.of(2023, 10, 6));
    RepeatTaskConfig repeatingConfig2 = new RepeatTaskConfig();
    repeatingConfig2.setRepeatType(RepeatType.YEARLY);
    repeatingConfig2.setRepeatInterval(1);
    YearlyRepeatOnConfig yearlyRepeatOnConfig = new YearlyRepeatOnConfig();
    Set<MonthDay> daysOfYear = new TreeSet<>();
    daysOfYear.add(MonthDay.of(10, 6));
    daysOfYear.add(MonthDay.of(6, 15));
    yearlyRepeatOnConfig.setDaysOfYear(daysOfYear);
    repeatingConfig2.setRepeatOn(yearlyRepeatOnConfig);
    task2.setRepeatingConfig(repeatingConfig2);

    crudService.saveUser(user);

    crudService.saveCategory(category1);
    crudService.saveCategory(category2);
    crudService.saveCategory(category3);
    crudService.saveCategory(category4);

    crudService.saveTask(task1);
    crudService.saveTask(task2);
  }
}
