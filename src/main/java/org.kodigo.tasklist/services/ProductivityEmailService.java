package services;

import java.util.List;

import utils.PDFGenerator;
import utils.RangeDates;
import models.Task;
import views.View;

import static services.ExternalServiceMethods.filterCompletedTasksByDate;

public class ProductivityEmailService {

  private ProductivityEmailService() {}

  public static void sendProductivityEmail(TaskService taskService, RangeDates rangeDates) {
    List<Task> tasks = filterCompletedTasksByDate(taskService.getCompletedTasks(), rangeDates);
    if (!tasks.isEmpty()) {

      PDFGenerator reportDocument = new PDFGenerator("Productivity report.pdf");
      reportDocument.generateProductivityPDF(tasks, rangeDates);
      EmailSenderService senderEmail = new EmailSenderService();
      senderEmail.sendEmailWithFile(
          "Productivity email",
          "Hi, here is your monthly productivity report :)",
          reportDocument.getNameDocument(),
          tasks.get(0).getUser().getEmail());
      View.message("The monthly productivity email was sent");
    } else {
      View.message("You don't have completed tasks in this month");
    }
  }
}
