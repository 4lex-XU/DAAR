package Performance;

import KMP.KMP;
import org.jfree.ui.RefineryUtilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerformanceKMP {

    public static void main(String[] arg) {
        List<String> paths = List.of(
                "Project1-automaton/Texts/Babylon.txt",
                "Project1-automaton/Texts/D_Europe_en_Amérique_par_le_pôle_nord.txt",
                "Project1-automaton/Texts/De_Duodecim_Abusionibus_Saeculi.txt",
                "Project1-automaton/Texts/Franc-Maçonnerie.txt",
                "Project1-automaton/Texts/PonyTracks.txt",
                "Project1-automaton/Texts/Sonniyhdistystä.txt",
                "Project1-automaton/Texts/The_literature_of_the_Highlanders.txt",
                "Project1-automaton/Texts/What_to_draw_and_how_to_draw_it.txt");

        String pattern = "qsdfqsdf";

        Runtime runtime = Runtime.getRuntime();
        Map<Integer, Double> datasetTime = new HashMap<>();
        Map<Integer, Double> datasetMemory = new HashMap<>();

        double sumTime = 0;
        double sumMem = 0;

        for (int i = 1; i <= 100; i++) {
            int nbCarac = 0;
            for (String path : paths) {
                String content = String.valueOf(KMP.extractContent(path));
                String text = content.repeat(i);
                nbCarac += text.length();

                KMP kmp = new KMP(pattern);
                runtime.gc();

                long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
                long startTime = System.nanoTime();
                kmp.search(text);
                long endTime = System.nanoTime();
                long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
                double duration = (endTime - startTime);
                double memoryUsed = (memoryAfter - memoryBefore);

                System.out.println("TOUR : " + i);
                System.out.println("Taille du texte : " + text.length());
                System.out.println("Temps d'exécution en nanoseconds : " + duration);
                System.out.println("Temps d'exécution en milliseconds : " + duration / 1000000.0);
                System.out.println("Mémoire utilisée (en mega) : " + memoryUsed);

                sumTime += (duration / 1000000.0);
                sumMem += memoryUsed;
            }
            datasetTime.put(nbCarac, sumTime / paths.size());
            datasetMemory.put(nbCarac, sumMem / paths.size());
        }

        ChartUtil chart = new ChartUtil("Performance de l'algorithme KMP");
        chart.createTimeChart("pattern: " + pattern, "Temps d'exécution (ms)", datasetTime);
        chart.createTimeChart("pattern: " + pattern, "Consommation mémoire (octets)", datasetMemory);
    }
}
