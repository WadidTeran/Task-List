package services;

import java.time.LocalDate;
import java.util.List;

import utils.PDFDocument;
import utils.RangeDates;
import models.Task;
import views.View;

import static services.ExternalServiceMethods.filterCompletedTasksByDate;

public class ProductivityEmailService {

  private ProductivityEmailService() {}

  public static void sendProductivityEmail(TaskService taskService) {
    LocalDate now = LocalDate.now();
    RangeDates rangeDates = new RangeDates(now.minusDays((long) now.getDayOfMonth() - 1), now);
    List<Task> tasks = filterCompletedTasksByDate(taskService.getCompletedTasks(), rangeDates);
    if (!tasks.isEmpty()) {
      View.message("Sending email...");

      PDFDocument reportDocument = new PDFDocument("Productivity report.pdf");
      reportDocument.createProductivityPDF(tasks, rangeDates);
      EmailSenderService senderEmail = new EmailSenderService();
      senderEmail.sendEmailWithFile(
          "Productivity email",
          "Hi, here we attach the monthly productivity report",
          reportDocument.getNameDocument(),
          tasks.get(0).getUser().getEmail());
      View.message("The monthly productivity email was sent");
    } else {
      View.message("No tienes tareas completadas en este mes");
    }
  }
}
