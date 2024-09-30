package KMP;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PerformanceKMP extends ApplicationFrame {

    public PerformanceKMP(String caption, String title, String label, Map<Integer, Long> inputData) {
        super(title);

        // create dataset
        XYSeries seriesTime = new XYSeries(label);
        for (Map.Entry<Integer, Long> entry : inputData.entrySet()) {
            seriesTime.add(entry.getKey(), entry.getValue());
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesTime);

        // create chart
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "Taille du texte",
                label,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // add subtitle
        TextTitle subtitle = new TextTitle(caption, new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setPosition(RectangleEdge.TOP);
        chart.addSubtitle(subtitle);

        // chart's layout
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesShapesVisible(0, false);
        plot.setRenderer(renderer);
        plot.getDomainAxis().setAutoRange(true);
        plot.getRangeAxis().setAutoRange(true);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        setContentPane(chartPanel);
    }

    public static void main(String[] arg) {
        int tour = 0;
        String repeat = "j'aimerais une pizza pepperonichevrefromage sans poivron";
        String pattern = "pepperonichevrefromage";

        Map<Integer, Long> datasetTime = new HashMap<>();
        Map<Integer, Long> datasetMemory = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();

        for (int i = 10; i < 10000; i += 100) {
            tour++;
            String text = repeat.repeat(i);

            KMP kmp = new KMP(pattern);
            runtime.gc();

            long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
            long startTime = System.nanoTime();
            kmp.search(text);
            long endTime = System.nanoTime();
            long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
            long duration = (endTime - startTime);
            long memoryUsed = (memoryAfter - memoryBefore);

            System.out.println("TOUR : " + tour);
            System.out.println("Texte de taille : " + text.length());
            System.out.println("Temps d'exécution en nanoseconds : " + duration);
            System.out.println("Temps d'exécution en milliseconds : " + duration / 1000000.0);
            System.out.println("Mémoire utilisée (en mega) : " + memoryUsed);

            datasetTime.put(text.length(), (long) (duration / 1000000.0));
            datasetMemory.put(text.length(), memoryUsed);
        }

        PerformanceKMP chartTime = new PerformanceKMP("text: " + repeat + "\n" + "pattern: " + pattern, "Performance de l'algorithme KMP", "Temps d'exécution (ms)", datasetTime);
        chartTime.pack();
        RefineryUtilities.centerFrameOnScreen(chartTime);
        chartTime.setVisible(true);

        PerformanceKMP chartMem = new PerformanceKMP("text: " + repeat + "\n" + "pattern: " + pattern, "Performance de l'algorithme KMP", "Consommation mémoire (octets)", datasetMemory);
        chartMem.pack();
        RefineryUtilities.centerFrameOnScreen(chartMem);
        chartMem.setVisible(true);
    }
}
