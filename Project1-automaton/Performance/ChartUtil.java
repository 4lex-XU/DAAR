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

    public void createTimeChart(String caption, String label, Map<Integer, Long> inputData) {
        // create dataset
        XYSeries seriesTime = new XYSeries(label);
        for (Map.Entry<Integer, Long> entry : inputData.entrySet()) {
            seriesTime.add(entry.getKey(), entry.getValue());
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesTime);

        // create chart
        JFreeChart chart = ChartFactory.createXYLineChart(
                super.getTitle(),
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

        JFrame frame = new JFrame(label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(chartPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        RefineryUtilities.centerFrameOnScreen(frame);
    }

    public void createHistogramChart(String label, Map<String, Double> inputData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Double> entry : inputData.entrySet()) {
            dataset.addValue(entry.getValue(), label, entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                super.getTitle(),
                "Regex",
                label,
                dataset
        );
        JPanel panel = new ChartPanel(chart);
        JFrame frame = new JFrame(label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
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
                "Valeur",
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
    public static double executeEgrep(String pattern, String fichier) throws IOException, InterruptedException {
        // Construire la commande egrep
        ProcessBuilder processBuilder = new ProcessBuilder("egrep", pattern, fichier);
        processBuilder.redirectErrorStream(true); // Fusionner stdout et stderr

        double elapsedTime = 0;
        try {
            // Enregistrer le temps de début en nanosecondes
            long startTime = System.nanoTime();

            // Démarrer le processus
            Process process = processBuilder.start();

            // Lire la sortie de egrep
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                System.out.println(ligne);
            }

            // Attendre la fin du processus
            int exitCode = process.waitFor();

            // Enregistrer le temps de fin en nanosecondes
            long endTime = System.nanoTime();

            // Calculer le temps écoulé en millisecondes avec décimales
            elapsedTime = (endTime - startTime) / 1_000_000.0;

            // Afficher le temps d'exécution avec précision décimale
            System.out.printf("Temps d'exécution : %.3f millisecondes%n", elapsedTime);

            // Vérifier si egrep s'est terminé correctement
            if (exitCode != 0) {
                System.err.println("La commande egrep s'est terminée avec le code de sortie " + exitCode);
                System.exit(exitCode);
            }

        } catch (IOException e) {
            System.err.println("Erreur lors de l'exécution de la commande egrep : " + e.getMessage());
            System.exit(1);
        } catch (InterruptedException e) {
            System.err.println("Le processus a été interrompu : " + e.getMessage());
            System.exit(1);
        }

        return elapsedTime;
    }

}
