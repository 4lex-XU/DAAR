import java.util.Scanner;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to Bogota, Mr. Thomas Anderson.");
        String regExStr;
        if (args.length != 0) {
            regExStr = args[0];
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("  >> Please enter a regEx: ");
            regExStr = scanner.next();
            scanner.close();
        }
        System.out.println("  >> Parsing regEx \"" + regExStr + "\".");
        System.out.println("  >> ...");

        if (regExStr.length() < 1) {
            System.err.println("  >> ERROR: empty regEx.");
        } else {
            System.out.print("  >> ASCII codes: [" + (int) regExStr.charAt(0));
            for (int i = 1; i < regExStr.length(); i++)
                System.out.print("," + (int) regExStr.charAt(i));
            System.out.println("].");
            try {
                RegEx regex = new RegEx(regExStr);
                RegExTree ret = regex.parse();
                System.out.println("  >> Tree result: " + ret.toString() + ".");

                // Build NFA from syntax tree
                Automaton nfa = RegExToNFA.buildAutomaton(ret);
                System.out.println("  >> NFA built.");
                nfa.display();

                // Determinize NFA to get DFA
                Automaton dfa = NfaToDfaConverter.convertToDFA(nfa);
                System.out.println("  >> DFA built.");
                dfa.display();

                // Minimize DFA
                Automaton minimizedDfa = DfaMinimizer.minimize(dfa);
                System.out.println("  >> Minimized DFA:");
                minimizedDfa.display();

                // Optionnel : Recherche d'un mot dans un texte
                // Vous pouvez décommenter les lignes suivantes si vous avez implémenté la méthode de recherche
                /*
                Scanner scanner = new Scanner(System.in);
                System.out.print("  >> Please enter a text to search: ");
                String text = scanner.nextLine();
                scanner.close();

                List<Integer> positions = minimizedDfa.search(text);

                if (positions.isEmpty()) {
                    System.out.println("  >> No matches found.");
                } else {
                    System.out.println("  >> Matches found at positions: " + positions);
                    for (int pos : positions) {
                        System.out.println("    Match at index " + pos + ": " + text.substring(pos));
                    }
                }
                */

            } catch (Exception e) {
                System.err.println("  >> ERROR: syntax error for regEx \"" + regExStr + "\".");
                e.printStackTrace();
            }
        }

        System.out.println("  >> ...");
        System.out.println("  >> Parsing completed.");
        System.out.println("Goodbye Mr. Anderson.");
    }
}
