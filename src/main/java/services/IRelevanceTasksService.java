package services;

import java.util.List;
import models.Relevance;
import models.Task;

public interface IRelevanceTasksService {
  List<Task> getRelevanceTasks(Relevance relevance);
}
