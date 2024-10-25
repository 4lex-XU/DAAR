package Performance;

import AhoUllmanMethod.*;
import KMP.KMP;
import org.jfree.ui.RefineryUtilities;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class PerformanceAutomaton {

    public static void main(String[] arg) throws Exception {
        List<String> paths = List.of(
                "Project1-automaton/Texts/Babylon.txt",
                "Project1-automaton/Texts/D_Europe_en_Amérique_par_le_pôle_nord.txt",
                "Project1-automaton/Texts/De_Duodecim_Abusionibus_Saeculi.txt",
                "Project1-automaton/Texts/Franc-Maçonnerie.txt",
                "Project1-automaton/Texts/PonyTracks.txt",
                "Project1-automaton/Texts/Sonniyhdistystä.txt",
                "Project1-automaton/Texts/The_literature_of_the_Highlanders.txt",
                "Project1-automaton/Texts/What_to_draw_and_how_to_draw_it.txt");

        String regExStr = "the(.)*of";

        Map<Integer, Double> dataset = new HashMap<>();

        double sumBuild = 0;
        double sumSearch = 0;
        String text = "";

        for (int i = 1; i <= 100; i++) {
            int nbCarac = 0;
            for (String path : paths) {
                String content = String.valueOf(KMP.extractContent(path));
                text = content.repeat(i);
                nbCarac += text.length();

                // --- Mesure du Temps de Parsing ---
                long startTimeParsing = System.nanoTime();
                // Parsing de l'expression régulière
                RegEx regex = new RegEx(regExStr);
                RegExTree ret = regex.parse();
                long endTimeParsing = System.nanoTime();
                double durationParsing = (endTimeParsing - startTimeParsing) / 1_000_000.0;

                // --- Mesure du Temps de Construction du NFA ---
                long startTimeNFA = System.nanoTime();
                // Construction du NFA à partir de l'arbre syntaxique
                Automaton nfa = RegExToNFA.buildAutomaton(ret);
                long endTimeNFA = System.nanoTime();
                double durationNFA = (endTimeNFA - startTimeNFA) / 1_000_000.0;

                // --- Mesure du Temps de Conversion du NFA en DFA ---
                long startTimeDFA = System.nanoTime();
                // Conversion du NFA en DFA
                Automaton dfa = NfaToDfaConverter.convertToDFA(nfa);
                long endTimeDFA = System.nanoTime();
                double durationDFA = (endTimeDFA - startTimeDFA) / 1_000_000.0;

                // --- Mesure du Temps de Minimisation du DFA ---
                long startTimeMinimization = System.nanoTime();
                // Minimisation du DFA
                Automaton minimizedDfa = DfaMinimizer.minimize(dfa);
                long endTimeMinimization = System.nanoTime();
                double durationMinimization = (endTimeMinimization - startTimeMinimization) / 1_000_000.0;

                // --- Mesure du Temps d'Optimisation des Transitions ---
                long startTimeOptimization = System.nanoTime();
                // Optionnel : Construire la map optimisée pour les transitions
                minimizedDfa.buildOptimizedTransitionMap();
                long endTimeOptimization = System.nanoTime();
                double durationOptimization = (endTimeOptimization - startTimeOptimization) / 1_000_000.0;

                // --- Mesure du Temps Total de Recherche ---
                long timeSearch = 0;

                // --- Mesure du Temps de Recherche ---
                long startTimeSearchLine = System.nanoTime();
                /*
                String lowerCaseText = text.toLowerCase(); // Convertir la ligne en minuscules
                List<Integer> positions = minimizedDfa.searchOptimized(lowerCaseText);
                */
                List<Integer> positions = minimizedDfa.searchOptimized(text);
                long endTimeSearchLine = System.nanoTime();
                timeSearch += (endTimeSearchLine - startTimeSearchLine);

                double durationSearch = (timeSearch) / 1_000_000.0;
                double durationBuild = durationParsing + durationNFA + durationDFA + durationMinimization + durationOptimization;
                double totalDuration = durationBuild + durationSearch;
                //System.out.println("  >> Total Build Time: " + durationBuild + " ms");
                //System.out.println("  >> Total Search Time: " + durationSearch + " ms");
                //System.out.println("  >> Total Time: " + totalDuration + " ms");

                sumBuild += durationBuild;
                sumSearch += durationSearch;
            }
            dataset.put(nbCarac, (sumBuild / paths.size()) + (sumSearch / paths.size()));
        }

        ChartUtil chart = new ChartUtil("Performance de l'automate");
        chart.createTimeChart("regex: " + regExStr, "Temps de recherche (ms)", dataset);
    }
}
