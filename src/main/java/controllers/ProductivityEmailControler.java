package controllers;

import java.util.List;

import services.EmailSenderService;
import utils.PDFDocument;
import utils.RangeDates;
import models.Task;

public class ProductivityEmailControler {

  private ProductivityEmailControler() {}

  public static void sendProductivityEmail(List<Task> tasks, RangeDates rangeDates) {
    PDFDocument reportDocument = new PDFDocument("Productivity report.pdf");
    reportDocument.createProductivityPDF(tasks, rangeDates);
    EmailSenderService senderEmail = new EmailSenderService();
    senderEmail.sendEmailWithFile(
        "Productivity email",
        "Hi, here we attach the monthly productivity report",
        reportDocument.getNameDocument(),
        tasks.get(0).getUser().getEmail());
  }
}
