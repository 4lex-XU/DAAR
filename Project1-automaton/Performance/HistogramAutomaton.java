package Performance;

import AhoUllmanMethod.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HistogramAutomaton {

    public static double executeEgrep(String regex, String fichier) throws Exception {
        // Construire la commande egrep
        ProcessBuilder builder = new ProcessBuilder("egrep", regex, fichier);
        
        // Rediriger l'erreur vers le flux d'entrée standard
        builder.redirectErrorStream(true);
        
        // Démarrer le processus
        long startTime = System.nanoTime();
        Process process = builder.start();
        
        // Lire la sortie (facultatif, mais nécessaire pour éviter le blocage si le buffer est plein)
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while (reader.readLine() != null) {
                // Vous pouvez traiter la sortie ici si nécessaire
            }
        }
        
        // Attendre la fin du processus
        boolean finished = process.waitFor(1, TimeUnit.HOURS); // Timeout d'une heure
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("egrep n'a pas terminé dans le délai imparti.");
        }
        long endTime = System.nanoTime();
        
        // Calculer la durée en millisecondes
        double duration = (endTime - startTime) / 1_000_000.0;
        return duration;
    }

    public static void main(String[] arg) throws Exception {
        Path path = Paths.get("Project1-automaton/AhoUllmanMethod/B.txt");
        List<String> lines = Files.readAllLines(path);
        String[] regExStr = {"a","a|b|c|d","a.b","ab*","(a|b)*","a|bc*","Sargon"};

        Map<String, Double> datasetSearch = new HashMap<>();
        Map<String, Double> datasetBuild = new HashMap<>();
        Map<String, Double> datasetEgrep = new HashMap<>();

        for (String reg : regExStr) {

            double durationEgrep = executeEgrep(reg, "Project1-automaton/AhoUllmanMethod/B.txt");
            System.out.println("  >> egrep Time: " + durationEgrep + " ms");

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
            datasetEgrep.put(reg, durationEgrep);

        }


        ChartUtil chartEgrep = new ChartUtil("Performance de l'automate");
        chartEgrep.createHistogramChart("Temps de recherche (ms)", datasetSearch,"Temps de egrep (ms)", datasetEgrep);

        ChartUtil chartSearch = new ChartUtil("Performance de l'automate");
        chartSearch.createHistogramChart("Temps de recherche (ms)", datasetSearch);

        ChartUtil chartBuild = new ChartUtil("Performance de l'automate");
        chartBuild.createHistogramChart("Temps de construction (ms)", datasetBuild);
    }
}
