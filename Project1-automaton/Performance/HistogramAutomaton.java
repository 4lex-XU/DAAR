package Performance;

import AhoUllmanMethod.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HistogramAutomaton {

    public static void main(String[] arg) throws Exception {
        List<Path> paths = List.of(
                Paths.get("Project1-automaton/Texts/Abroad_with_Mark_Twain_and_Eugene_Field.txt"),
                Paths.get("Project1-automaton/Texts/Babylon.txt"),
                Paths.get("Project1-automaton/Texts/D_Europe_en_Amérique_par_le_pôle_nord.txt"),
                Paths.get("Project1-automaton/Texts/De_Duodecim_Abusionibus_Saeculi.txt"),
                Paths.get("Project1-automaton/Texts/Franc-Maçonnerie.txt"),
                Paths.get("Project1-automaton/Texts/In_Monsun_und_Pori.txt"),
                Paths.get("Project1-automaton/Texts/PonyTracks.txt"),
                Paths.get("Project1-automaton/Texts/Sonniyhdistystä.txt"),
                Paths.get("Project1-automaton/Texts/The_literature_of_the_Highlanders.txt"),
                Paths.get("Project1-automaton/Texts/What_to_draw_and_how_to_draw_it.txt"));

        String[] regExStr = {"a", "a|b|c|d", "a.b", "ab*", "(a|b)*", "a|bc*", "Sargon"};

        Map<String, Double> datasetAutomaton = new HashMap<>();
        Map<String, Double> datasetEgrep = new HashMap<>();

        double sumBuild = 0;
        double sumSearch = 0;
        double sumEgrep = 0;

        for (String reg : regExStr) {
            for (Path path : paths) {
                List<String> lines = Files.readAllLines(path);

                // --- Mesure du Temps de egrep ---
                double durationEgrep = ChartUtil.executeEgrep(reg, path.toString());

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
                    /*
                    String lowerCaseLine = line.toLowerCase(); // Convertir la ligne en minuscules
                    List<Integer> positions = minimizedDfa.searchOptimized(lowerCaseLine);
                    */
                    List<Integer> positions = minimizedDfa.searchOptimized(line);
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

                sumBuild += durationBuild;
                sumSearch += durationSearch;
                sumEgrep += durationEgrep;
            }

            datasetAutomaton.put(reg, (sumBuild / paths.size()) + (sumSearch / paths.size()));
            datasetEgrep.put(reg, sumEgrep / paths.size());
        }

        ChartUtil chart = new ChartUtil("Histogramme de comparaison des performances de recherche (ms)");
        chart.createHistogramChart("automate", datasetAutomaton, "egrep", datasetEgrep);
    }
}
