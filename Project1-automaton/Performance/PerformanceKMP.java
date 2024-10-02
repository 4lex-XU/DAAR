package Performance;

import KMP.KMP;
import org.jfree.ui.RefineryUtilities;

import java.util.HashMap;
import java.util.Map;

public class PerformanceKMP {

    public static void main(String[] arg) {
        int tour = 0;
        String repeat = "j'aimerais une pizza pepperonichevrefromage sans poivron";
        String pattern = "pepperonichevrefromage";

        Map<Integer, Long> datasetTime = new HashMap<>();
        Map<Integer, Long> datasetMemory = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();

        for (int i = 10; i < 1000000; i += 1000) {
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

        ChartUtil chartTime = new ChartUtil("Performance de l'algorithme KMP");
        chartTime.createTimeChart("text: " + repeat + "\n" + "pattern: " + pattern, "Temps d'exécution (ms)", datasetTime);

        ChartUtil chartMem = new ChartUtil("Performance de l'algorithme KMP");
        chartMem.createTimeChart("text: " + repeat + "\n" + "pattern: " + pattern, "Consommation mémoire (octets)", datasetMemory);
    }
}
