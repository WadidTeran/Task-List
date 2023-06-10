package utils;

import models.Relevance;
import models.Task;
import org.jfree.data.category.DefaultCategoryDataset;

import java.util.List;

import static utils.ExternalUtilityMethods.countTaskByRelevance;

public class DatasetGenerator {
  private DatasetGenerator() {}

  public static DefaultCategoryDataset generateRelevanceDataset(List<Task> tasks) {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    int[] numberRelevance = countTaskByRelevance(tasks);
    for (int i = 0; i < numberRelevance.length; i++) {
      dataset.addValue(numberRelevance[i], Relevance.values()[i], "");
    }
    return dataset;
  }
}
