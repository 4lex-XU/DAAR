package Performance;

import AhoUllmanMethod.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistogramAutomaton {

    public static void main(String[] arg) throws Exception {
        Path path = Paths.get("Project1-automaton/AhoUllmanMethod/B.txt");
        List<String> lines = Files.readAllLines(path);
        String[] regExStr = {"a","a|b|c|d","a.b","ab*","(a|b)*","a|bc*","Sargon"};

        Map<String, Double> datasetSearch = new HashMap<>();
        Map<String, Double> datasetBuild = new HashMap<>();

        for (String reg : regExStr) {

            // --- Mesure du Temps de Parsing ---
            long startTimeParsing = System.nanoTime();
            // Parsing de l'expression régulière
            RegEx regex = new RegEx(reg);
            RegExTree ret = regex.parse();
            long endTimeParsing = System.nanoTime();
            double durationParsing = (endTimeParsing - startTimeParsing) / 1_000_000.0;
            System.out.println("  >> Parsing Time: " + durationParsing + " ms");

            System.out.println("  >> Tree result: " + ret.toString() + ".");

            // --- Mesure du Temps de Construction du NFA ---
            long startTimeNFA = System.nanoTime();
            // Construction du NFA à partir de l'arbre syntaxique
            Automaton nfa = RegExToNFA.buildAutomaton(ret);
            long endTimeNFA = System.nanoTime();
            double durationNFA = (endTimeNFA - startTimeNFA) / 1_000_000.0;
            System.out.println("  >> NFA built in " + durationNFA + " ms.");
            nfa.display();

            // --- Mesure du Temps de Conversion du NFA en DFA ---
            long startTimeDFA = System.nanoTime();
            // Conversion du NFA en DFA
            Automaton dfa = NfaToDfaConverter.convertToDFA(nfa);
            long endTimeDFA = System.nanoTime();
            double durationDFA = (endTimeDFA - startTimeDFA) / 1_000_000.0;
            System.out.println("  >> DFA built in " + durationDFA + " ms.");
            dfa.display();

            // --- Mesure du Temps de Minimisation du DFA ---
            long startTimeMinimization = System.nanoTime();
            // Minimisation du DFA
            Automaton minimizedDfa = DfaMinimizer.minimize(dfa);
            long endTimeMinimization = System.nanoTime();
            double durationMinimization = (endTimeMinimization - startTimeMinimization) / 1_000_000.0;
            System.out.println("  >> Minimized DFA built in " + durationMinimization + " ms.");
            minimizedDfa.display();

            // --- Mesure du Temps d'Optimisation des Transitions ---
            long startTimeOptimization = System.nanoTime();
            // Optionnel : Construire la map optimisée pour les transitions
            minimizedDfa.buildOptimizedTransitionMap();
            long endTimeOptimization = System.nanoTime();
            double durationOptimization = (endTimeOptimization - startTimeOptimization) / 1_000_000.0;
            System.out.println("  >> Transition Map optimized in " + durationOptimization + " ms.");

            // --- Mesure du Temps Total de Recherche ---
            long timeSearch = 0;
            // Recherche de correspondances dans chaque ligne du fichier texte
            System.out.println("  >> Searching for matches in the text.");
            int lineNumber = 1;
            boolean anyMatch = false;
            for (String line : lines) {

                // --- Mesure du Temps de Recherche par Ligne ---
                long startTimeSearchLine = System.nanoTime();
                String lowerCaseLine = line.toLowerCase(); // Convertir la ligne en minuscules
                List<Integer> positions = minimizedDfa.searchOptimized(lowerCaseLine);
                long endTimeSearchLine = System.nanoTime();
                timeSearch += (endTimeSearchLine - startTimeSearchLine);
                lineNumber++;
            }

            double durationSearch = (timeSearch) / 1_000_000.0;
            double durationBuild = durationParsing + durationNFA + durationDFA + durationMinimization + durationOptimization;
            double totalDuration = durationBuild + durationSearch;
            System.out.println("  >> Total Build Time: " + durationBuild + " ms");
            System.out.println("  >> Total Search Time: " + durationSearch + " ms");
            System.out.println("  >> Total Time: " + totalDuration + " ms");

            datasetSearch.put(reg, durationSearch);
            datasetBuild.put(reg, durationBuild);

        }

        ChartUtil chartSearch = new ChartUtil("Performance de l'automate");
        chartSearch.createHistogramChart("Temps de recherche (ms)", datasetSearch);

        ChartUtil chartBuild = new ChartUtil("Performance de l'automate");
        chartBuild.createHistogramChart("Temps de construction (ms)", datasetBuild);
    }
}
