package Performance;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class ChartUtil extends ApplicationFrame {

    public ChartUtil(String title) {
        super(title);
    }

    public void createTimeChart(String caption, String label, Map<Integer, Double> inputData) {
        // création du jeu de données
        XYSeries seriesTime = new XYSeries(label);
        for (Map.Entry<Integer, Double> entry : inputData.entrySet()) {
            seriesTime.add(entry.getKey(), entry.getValue());
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesTime);

        // création du graphe
        JFreeChart chart = ChartFactory.createXYLineChart(
                super.getTitle(),
                "Nombres de caractères",
                label,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // ajout sous titre
        TextTitle subtitle = new TextTitle(caption, new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setPosition(RectangleEdge.TOP);
        chart.addSubtitle(subtitle);

        // Mis en page du graphe
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

        JFrame frame = new JFrame(label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(chartPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        RefineryUtilities.centerFrameOnScreen(frame);
    }

    public void createHistogramChart(String label1, Map<String, Double> inputData1,
                                     String label2, Map<String, Double> inputData2) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Ajout de la première série de données
        for (Map.Entry<String, Double> entry : inputData1.entrySet()) {
            dataset.addValue(entry.getValue(), label1, entry.getKey());
        }

        // Ajout de la deuxième série de données
        for (Map.Entry<String, Double> entry : inputData2.entrySet()) {
            dataset.addValue(entry.getValue(), label2, entry.getKey());
        }

        // Création du graphique
        JFreeChart chart = ChartFactory.createBarChart(
                super.getTitle(),
                "Regex",
                "Temps de d'exécution (ms)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Personnalisation du graphique
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.BLUE);

        // Affichage dans un JFrame
        JPanel panel = new ChartPanel(chart);
        JFrame frame = new JFrame("Histogrammes Comparatifs");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Méthode pour mesurer le temps d'exécution de egrep avec un pattern et un fichier
    public static double executeEgrep(String pattern, String fichier, int iterations) throws IOException, InterruptedException {
        double totalDuration = 0.0;

        for (int i = 0; i < iterations; i++) {
            // Construire la commande egrep
            ProcessBuilder processBuilder = new ProcessBuilder("egrep", pattern, fichier);
            // Rediriger la sortie standard vers /dev/null pour éviter de lire les résultats
            processBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD);

            // Enregistrer le temps de début en nanosecondes
            long startTime = System.nanoTime();

            // Démarrer le processus
            Process process = processBuilder.start();

            // Attendre la fin du processus
            int exitCode = process.waitFor();

            // Enregistrer le temps de fin en nanosecondes
            long endTime = System.nanoTime();

            // Calculer le temps écoulé en millisecondes
            double elapsedTime = (endTime - startTime) / 1_000_000.0;

            // Accumuler le temps écoulé
            totalDuration += elapsedTime;
        }

        // Calculer la moyenne des durées
        return iterations > 0 ? totalDuration / iterations : Double.NaN;
    }
}
