package Performance;

import AhoUllmanMethod.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistogramAutomaton {

    public static void main(String[] args) throws Exception {
        List<Path> paths = List.of(
                Paths.get("Project1-automaton/Texts/Babylon.txt"),
                Paths.get("Project1-automaton/Texts/D_Europe_en_Amérique_par_le_pôle_nord.txt"),
                Paths.get("Project1-automaton/Texts/De_Duodecim_Abusionibus_Saeculi.txt"),
                Paths.get("Project1-automaton/Texts/Franc-Maçonnerie.txt"),
                Paths.get("Project1-automaton/Texts/PonyTracks.txt"),
                Paths.get("Project1-automaton/Texts/Sonniyhdistystä.txt"),
                Paths.get("Project1-automaton/Texts/The_literature_of_the_Highlanders.txt"),
                Paths.get("Project1-automaton/Texts/What_to_draw_and_how_to_draw_it.txt"));
        // Expressions régulières à tester
        String[] regExStr = {"a", "a|b|c|d", "a.b", "ab*", "(a|b)*", "a|bc*", "Sargon"};
        Map<String, Double> datasetAutomaton = new HashMap<>();
        Map<String, Double> datasetEgrep = new HashMap<>();

        // Nombre d'itérations pour la moyenne
        int iterations = 1;

        for (String reg : regExStr) {
            double sumAutomaton = 0.0;
            double sumEgrep = 0.0;

            for (Path path : paths) {
                List<String> lines = Files.readAllLines(path);
                double averageTotalTime = 0;
                double averageBuildTime = 0;
                double averageSearchTime = 0;

                try {
                    for (int i = 0; i < iterations; i++) {
                        double durationSearch = 0;
                        double durationBuild = 0;
                        // --- Mesure du Temps de Parsing ---
                        long startTimeParsing = System.nanoTime();
                        // Parsing de l'expression régulière
                        RegEx regex = new RegEx(reg);
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
                        // Recherche de correspondances dans chaque ligne du fichier texte
                        int lineNumber = 1;
                        boolean anyMatch = false;
                        for (String line : lines) {

                            // --- Mesure du Temps de Recherche par Ligne ---
                            long startTimeSearchLine = System.nanoTime();
                            //String lowerCaseLine = line.toLowerCase();
                            //List<Integer> positions = minimizedDfa.searchOptimized(lowerCaseLine);
                            List<Integer> positions = minimizedDfa.searchOptimized(line);
                            long endTimeSearchLine = System.nanoTime();
                            timeSearch += (endTimeSearchLine - startTimeSearchLine);

                            if (!positions.isEmpty()) {
                                anyMatch = true;
                                //System.out.println(line);
                            }
                            lineNumber++;

                            if (!anyMatch) {
                                //System.out.println("  >> No matches found in the entire text. " + path.toString());
                            }
                        }
                        durationBuild = durationParsing + durationNFA + durationDFA + durationMinimization + durationOptimization;
                        durationSearch = (timeSearch) / 1_000_000.0;

                        averageSearchTime += durationSearch;
                        averageBuildTime += durationBuild;
                    }
                    averageTotalTime = (averageBuildTime / iterations) + (averageSearchTime / iterations);
                } catch (Exception e) {
                    System.err.println("  >> ERREUR: Syntax error for regEx \"" + regExStr + "\".");
                    e.printStackTrace();
                }
                sumEgrep += ChartUtil.executeEgrep(reg, path.toString(), iterations);
                sumAutomaton += averageTotalTime;
            }
            System.out.println("  >> Total Time Automaton: " + sumAutomaton + " ms");
            System.out.println("  >> Total Egrep: " + sumEgrep + " ms");
            datasetAutomaton.put(reg, sumAutomaton);
            datasetEgrep.put(reg, sumEgrep);
        }
        // Créer le graphique
        ChartUtil chart = new ChartUtil("Histogramme de comparaison des performances de recherche en ms avec "+ iterations+ " itération(s)");
        chart.createHistogramChart("automate", datasetAutomaton, "egrep", datasetEgrep);

        System.out.println("  >> ...");
        System.out.println("  >> Histogramme généré.");
        System.out.println("Goodbye Mr. Anderson.");
    }
}