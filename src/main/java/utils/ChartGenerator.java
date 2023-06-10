package utils;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.IOException;

public class ChartGenerator {

  private ChartGenerator() {}

  public static Image generateBarChart(String[] titles, DefaultCategoryDataset dataset)
      throws IOException, BadElementException {
    String chartImagePath = "chart.png";
    JFreeChart chart =
        ChartFactory.createBarChart(
            titles[0], titles[1], titles[2], dataset, PlotOrientation.VERTICAL, true, false, false);

    CategoryPlot plot = chart.getCategoryPlot();
    BarRenderer renderer = (BarRenderer) plot.getRenderer();
    renderer.setMaximumBarWidth(0.1);

    NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
    yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

    ChartUtilities.saveChartAsPNG(new java.io.File(chartImagePath), chart, 600, 400);

    Image chartImage = Image.getInstance(chartImagePath);
    chartImage.setAlignment(Element.ALIGN_CENTER);
    chartImage.scaleToFit(450, 450);

    return chartImage;
  }
}
