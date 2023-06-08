package services;

import java.time.LocalDate;
import java.util.ArrayList;
import models.Category;
import models.Relevance;
import models.Task;
import repositories.IRepository;

public class FilteredTaskSearchService
    implements ICategoryTasksService,
        ICompletedTasksService,
        IPendingTasksService,
        IRelevanceTasksService {

  private final IRepository<Task> taskRepository;

  public FilteredTaskSearchService(IRepository<Task> taskRepository) {
    this.taskRepository = taskRepository;
  }

  @Override
  public ArrayList<Task> getCategoryTasks(Category category) {
    return (ArrayList<Task>)
        getAllPendingTasks().stream()
            .filter(
                t -> {
                  if (t.getCategory() != null) {
                    return t.getCategory().getName().equals(category.getName());
                  }
                  return false;
                })
            .toList();
  }

  @Override
  public ArrayList<Task> getCompletedTasks() {
    return (ArrayList<Task>) taskRepository.findAll().stream().filter(Task::isCompleted).toList();
  }

  @Override
  public ArrayList<Task> getFuturePendingTasks() {
    return (ArrayList<Task>)
        getAllPendingTasks().stream()
            .filter(
                t -> {
                  if (t.getDueDate() != null) {
                    return t.getDueDate().isAfter(LocalDate.now());
                  }
                  return false;
                })
            .toList();
  }

  @Override
  public ArrayList<Task> getPastPendingTasks() {
    return (ArrayList<Task>)
        getAllPendingTasks().stream()
            .filter(
                t -> {
                  if (t.getDueDate() != null) {
                    return t.getDueDate().isBefore(LocalDate.now());
                  }
                  return false;
                })
            .toList();
  }

  @Override
  public ArrayList<Task> getPendingTasksForToday() {
    return (ArrayList<Task>)
        getAllPendingTasks().stream()
            .filter(
                t -> {
                  if (t.getDueDate() != null) {
                    return t.getDueDate().isEqual(LocalDate.now());
                  }
                  return false;
                })
            .toList();
  }

  @Override
  public ArrayList<Task> getAllPendingTasks() {
    return (ArrayList<Task>)
        taskRepository.findAll().stream().filter(t -> !t.isCompleted()).toList();
  }

  @Override
  public ArrayList<Task> getRelevanceTasks(Relevance relevance) {
    return (ArrayList<Task>)
        getAllPendingTasks().stream().filter(t -> t.getRelevance() == relevance).toList();
  }
}
