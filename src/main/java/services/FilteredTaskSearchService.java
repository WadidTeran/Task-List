package services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;
import models.Category;
import models.Relevance;
import models.Task;
import repositories.IRepository;
import repositories.TaskRepository;

public class FilteredTaskSearchService
    implements ICategoryTasksService,
        ICompletedTasksService,
        IPendingTasksService,
        IRelevanceTasksService {

  private final IRepository<Task> taskRepository;

  public FilteredTaskSearchService() {
    this.taskRepository = new TaskRepository();
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
            .collect(Collectors.toList());
  }

  @Override
  public ArrayList<Task> getCompletedTasks() {
    return (ArrayList<Task>)
        taskRepository.findAll().stream().filter(Task::isCompleted).collect(Collectors.toList());
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
            .collect(Collectors.toList());
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
            .collect(Collectors.toList());
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
            .collect(Collectors.toList());
  }

  @Override
  public ArrayList<Task> getAllPendingTasks() {
    return (ArrayList<Task>)
        taskRepository.findAll().stream()
            .filter(t -> !t.isCompleted())
            .collect(Collectors.toList());
  }

  @Override
  public ArrayList<Task> getRelevanceTasks(Relevance relevance) {
    return (ArrayList<Task>)
        getAllPendingTasks().stream()
            .filter(t -> t.getRelevance() == relevance)
            .collect(Collectors.toList());
  }
}
