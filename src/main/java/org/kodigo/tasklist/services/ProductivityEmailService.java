package org.kodigo.tasklist.services;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.kodigo.tasklist.models.Task;
import org.kodigo.tasklist.utils.PDFGenerator;
import org.kodigo.tasklist.utils.RangeDates;
import org.kodigo.tasklist.utils.Warnings;
import org.kodigo.tasklist.views.View;

public class ProductivityEmailService {
  private final TaskService taskService;
  private final EmailSenderService senderEmail;

  public ProductivityEmailService(TaskService taskService, EmailSenderService senderEmail) {
    this.taskService = taskService;
    this.senderEmail = senderEmail;
  }

  public void askForRange() {
    String startDateStr = View.input("Start date (yyyy-MM-dd)");
    if (startDateStr == null) return;
    else if (startDateStr.isBlank() || startDateStr.isEmpty()) {
      View.message(Warnings.EMPTY_INPUT);
      return;
    }
    String endDateStr = View.input("End date (yyyy-MM-dd)");
    if (endDateStr == null) return;
    else if (endDateStr.isBlank() || endDateStr.isEmpty()) {
      View.message(Warnings.EMPTY_INPUT);
      return;
    }

    try {
      LocalDate startDate = LocalDate.parse(startDateStr);
      LocalDate endDate = LocalDate.parse(endDateStr);
      if (!startDate.isAfter(endDate)) {
        RangeDates rangeDates = new RangeDates(startDate, endDate);
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
          Runnable emailSendingProcess = () -> sendProductivityEmail(rangeDates);
          executor.execute(emailSendingProcess);
        }
      } else {
        View.message("¡Start date must be before end date!");
      }
    } catch (DateTimeParseException e) {
      View.message("Invalid date format");
    }
  }

  public void sendProductivityEmail(RangeDates rangeDates) {
    List<Task> tasks =
        ExternalServiceMethods.filterCompletedTasksByDate(
            taskService.getCompletedTasks(), rangeDates);
    if (!tasks.isEmpty()) {

      PDFGenerator reportDocument = new PDFGenerator("Productivity report.pdf");
      reportDocument.generateProductivityPDF(tasks, rangeDates);
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
