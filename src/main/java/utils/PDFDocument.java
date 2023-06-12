package utils;

import static utils.ExternalUtilityMethods.generateTitle;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;
import models.Relevance;
import models.Task;

public class PDFDocument {

  private final Document document;
  @Getter private final String nameDocument;

  public PDFDocument(String nameDocument) {
    document = new Document();
    this.nameDocument = "./" + nameDocument;
  }

  public void generateBasicPDF(String title, String textBody) {

    try {
      String path = new File(".").getCanonicalPath();
      String fileName = path + nameDocument;

      PdfWriter.getInstance(document, new FileOutputStream(fileName));

      document.open();
      Paragraph pdfTitle = new Paragraph(title);
      pdfTitle.setAlignment(Element.ALIGN_JUSTIFIED);

      Paragraph body = new Paragraph(textBody);

      Paragraph p3 = new Paragraph("Task-List");

      Font f = new Font();
      f.setFamily(Font.FontFamily.COURIER.name());
      f.setStyle(Font.BOLDITALIC);
      f.setSize(8);

      p3.setFont(f);
      f.setSize(10);

      document.add(pdfTitle);
      document.add(body);
      document.add(p3);
      document.close();
    } catch (Exception ex) {
      Logger.getLogger(PDFDocument.class.getName())
          .log(Level.SEVERE, " Error trying to generate a document ", ex);
    }
  }

  public void createProductivityPDF(List<Task> tasks, RangeDates rangeDates) {

    try {
      String path = new File(nameDocument).getCanonicalPath();
      String[] headersRelevance = {
        "NAME", "COMPLETED DATE", "SPECIFIED TIME", "DESCRIPTION", "CATEGORY", "REPETITIVE"
      };
      String[] titlesChart = {"PRODUCTIVITY GRAPH", "Relevance", "Completed tasks"};
      float[] widthsRelevance = {18f, 15f, 14f, 20f, 20f, 13};

      PdfWriter.getInstance(document, new FileOutputStream(path));
      document.open();

      Paragraph dateRange =
          new Paragraph(
              "From "
                  + rangeDates.startDate().toString()
                  + " to "
                  + rangeDates.endDate().toString());
      dateRange.setAlignment(Element.ALIGN_RIGHT);
      dateRange.setSpacingAfter(30);

      Paragraph title = new Paragraph("Productivity report");
      title.setAlignment(Element.ALIGN_JUSTIFIED);

      Paragraph signature = new Paragraph("Task-List " + LocalDate.now());
      signature.setAlignment(Element.ALIGN_RIGHT);

      document.add(title);
      document.add(dateRange);
      document.add(
          ChartGenerator.generateBarChart(
              titlesChart, ChartGenerator.generateRelevanceDataset(tasks)));
      File chartImageFile = new File("chart.png");
      if (chartImageFile.exists()) {
        chartImageFile.deleteOnExit();
      }
      for (int i = 0; i < Relevance.values().length; i++) {
        PDFTable table = new PDFTable(headersRelevance, widthsRelevance);
        document.add(generateTitle("COMPLETED TASKS BY " + Relevance.values()[i] + " RELEVANCE"));
        document.add(table.fillTaskTablesByRelevance(tasks, Relevance.values()[i]));
      }
      document.add(signature);
      document.close();
    } catch (Exception ex) {
      Logger.getLogger(PDFDocument.class.getName())
          .log(Level.SEVERE, " Error trying to generate a document ", ex);
    }
  }
}
