package services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import models.Category;
import models.Relevance;
import models.Task;

public class FilteredTaskSearchService
    implements ICategoryTasksService,
        ICompletedTasksService,
        IPendingTasksService,
        IRelevanceTasksService {

  private final ICRUDService crudService;

  public FilteredTaskSearchService(ICRUDService crudService) {
    this.crudService = crudService;
  }

  @Override
  public List<Task> getCategoryTasks(Category category) {
    return getAllPendingTasks().stream()
        .filter(
            t -> {
              if (t.getCategory() != null) {
                return t.getCategory().getName().equals(category.getName());
              }
              return false;
            })
        .collect(Collectors.toList());
  }

  @Override
  public List<Task> getCompletedTasks() {
    return crudService.findAllTasks().stream()
        .filter(Task::isCompleted)
        .collect(Collectors.toList());
  }

  @Override
  public List<Task> getFuturePendingTasks() {
    return getAllPendingTasks().stream()
        .filter(
            t -> {
              if (t.getDueDate() != null) {
                return t.getDueDate().isAfter(LocalDate.now());
              }
              return false;
            })
        .collect(Collectors.toList());
  }

  @Override
  public List<Task> getPastPendingTasks() {
    return getAllPendingTasks().stream()
        .filter(
            t -> {
              if (t.getDueDate() != null) {
                return t.getDueDate().isBefore(LocalDate.now());
              }
              return false;
            })
        .collect(Collectors.toList());
  }

  @Override
  public List<Task> getPendingTasksForToday() {
    return getAllPendingTasks().stream()
        .filter(
            t -> {
              if (t.getDueDate() != null) {
                return t.getDueDate().isEqual(LocalDate.now());
              }
              return false;
            })
        .collect(Collectors.toList());
  }

  @Override
  public List<Task> getAllPendingTasks() {
    return crudService.findAllTasks().stream()
        .filter(t -> !t.isCompleted())
        .collect(Collectors.toList());
  }

  @Override
  public List<Task> getRelevanceTasks(Relevance relevance) {
    return getAllPendingTasks().stream()
        .filter(t -> t.getRelevance() == relevance)
        .collect(Collectors.toList());
  }
}
