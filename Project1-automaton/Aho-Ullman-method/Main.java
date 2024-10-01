import java.util.Scanner;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to Bogota, Mr. Thomas Anderson.");
        String regExStr = "";
        String textFilePath = "";

        // Lecture des arguments de ligne de commande
        if (args.length >= 2) {
            regExStr = args[0].toLowerCase(); // Convertir en minuscules
            textFilePath = args[1];
        } else {
            // Si les arguments ne sont pas fournis, demander à l'utilisateur
            Scanner scanner = new Scanner(System.in);
            if (args.length < 1) {
                System.out.print("  >> Veuillez entrer une expression régulière: ");
                regExStr = scanner.nextLine().toLowerCase(); // Convertir en minuscules
            } else {
                regExStr = args[0].toLowerCase(); // Convertir en minuscules
            }

            System.out.print("  >> Veuillez entrer le chemin vers le fichier texte: ");
            textFilePath = scanner.nextLine();
            scanner.close();
        }

        // Lecture du contenu du fichier texte
        List<String> lines;
        try {
            Path path = Paths.get(textFilePath);
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            System.err.println("  >> ERREUR: Impossible de lire le fichier \"" + textFilePath + "\".");
            e.printStackTrace();
            return;
        }

        System.out.println("  >> Parsing de l'expression régulière \"" + regExStr + "\".");
        System.out.println("  >> ...");

        if (regExStr.length() < 1) {
            System.err.println("  >> ERREUR: Expression régulière vide.");
            return;
        } else {
            System.out.print("  >> Codes ASCII: [" + (int) regExStr.charAt(0));
            for (int i = 1; i < regExStr.length(); i++)
                System.out.print("," + (int) regExStr.charAt(i));
            System.out.println("].");
            try {
                // --- Mesure du Temps de Parsing ---
                long startTimeParsing = System.nanoTime();
                // Parsing de l'expression régulière
                RegEx regex = new RegEx(regExStr);
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

                    if (!positions.isEmpty()) {
                        anyMatch = true;
                        System.out.println("  >> Matches found in line " + lineNumber + " at positions: " + positions);
                        for (int pos : positions) {
                            // Afficher un extrait du texte autour de la position trouvée
                            int start = Math.max(pos - 10, 0);
                            int end = Math.min(pos + 10, lowerCaseLine.length());
                            String excerpt = line.substring(start, end).replaceAll("\\s+", " ");
                            System.out.println("    Match at index " + pos + ": " + excerpt);
                        }
                    }
                    lineNumber++;
                }
                double durationSearch = (timeSearch) / 1_000_000.0;
                double durationBuild = durationParsing + durationNFA + durationDFA + durationMinimization + durationOptimization;
                double totalDuration = durationBuild + durationSearch;
                System.out.println("  >> Total Build Time: " + durationBuild + " ms");
                System.out.println("  >> Total Search Time: " + durationSearch + " ms");
                System.out.println("  >> Total Time: " + totalDuration + " ms");

                if (!anyMatch) {
                    System.out.println("  >> No matches found in the entire text.");
                }

            } catch (Exception e) {
                System.err.println("  >> ERREUR: Syntax error for regEx \"" + regExStr + "\".");
                e.printStackTrace();
            }
        }

        System.out.println("  >> ...");
        System.out.println("  >> Parsing completed.");
        System.out.println("Goodbye Mr. Anderson.");
    }
}
