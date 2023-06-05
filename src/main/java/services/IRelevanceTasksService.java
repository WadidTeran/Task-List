package services;

import java.util.ArrayList;
import models.Relevance;
import models.Task;

public interface IRelevanceTasksService {
  ArrayList<Task> getRelevanceTasks(Relevance relevance);
}
