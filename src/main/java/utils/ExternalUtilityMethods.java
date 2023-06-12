package utils;

import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import models.Relevance;
import models.Task;

import java.util.List;

public class ExternalUtilityMethods {
  private ExternalUtilityMethods() {}

  public static int[] countTaskByRelevance(List<Task> tasks) {
    int[] tasksByRelevance = new int[4];
    for (Task task : tasks) {
      switch (task.getRelevance()) {
        case HIGH -> tasksByRelevance[Relevance.HIGH.ordinal()]++;
        case MEDIUM -> tasksByRelevance[Relevance.MEDIUM.ordinal()]++;
        case LOW -> tasksByRelevance[Relevance.LOW.ordinal()]++;
        case NONE -> tasksByRelevance[Relevance.NONE.ordinal()]++;
      }
    }
    return tasksByRelevance;
  }

  public static Paragraph generateTitle(String text) {
    Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    Paragraph title = new Paragraph(text, titleFont);
    title.setAlignment(Element.ALIGN_CENTER);
    title.setSpacingAfter(10);
    title.setSpacingBefore(30);
    return title;
  }

}
