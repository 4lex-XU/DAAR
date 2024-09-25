import java.util.Scanner;

public class RegExToNFA {
    public static void main(String[] args) {
        System.out.println("Welcome to Bogota, Mr. Thomas Anderson.");
        String regExStr;
        if (args.length != 0) {
            regExStr = args[0];
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("  >> Please enter a regEx: ");
            regExStr = scanner.next();
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
                Automaton nfa = buildAutomaton(ret);
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

            } catch (Exception e) {
                System.err.println("  >> ERROR: syntax error for regEx \"" + regExStr + "\".");
                e.printStackTrace();
            }
        }

        System.out.println("  >> ...");
        System.out.println("  >> Parsing completed.");
        System.out.println("Goodbye Mr. Anderson.");
    }

    // Build NFA from syntax tree
    public static Automaton buildAutomaton(RegExTree tree) throws Exception {
        if (tree.subTrees.isEmpty()) {
            return basicAutomaton(tree.root);
        }
        switch (tree.root) {
            case RegEx.CONCAT:
                return concatenate(buildAutomaton(tree.subTrees.get(0)), buildAutomaton(tree.subTrees.get(1)));
            case RegEx.ALTERN:
                return alternate(buildAutomaton(tree.subTrees.get(0)), buildAutomaton(tree.subTrees.get(1)));
            case RegEx.ETOILE:
                return kleeneStar(buildAutomaton(tree.subTrees.get(0)));
            default:
                throw new Exception("Unknown operator");
        }
    }

    // Basic automaton for single symbol
    private static Automaton basicAutomaton(int root) {
        State start = new State();
        State end = new State();
        Automaton automaton = new Automaton(start, end);
        if (root == RegEx.DOT) {
            // DOT represents any character
            for (char c = 'a'; c <= 'z'; c++) {
                automaton.addTransition(new Transition(start, end, c));
            }
            for (char c = 'A'; c <= 'Z'; c++) {
                automaton.addTransition(new Transition(start, end, c));
            }
            // Ajouter d'autres symboles si nécessaire
        } else {
            automaton.addTransition(new Transition(start, end, (char) root));
        }
        return automaton;
    }

    // Concatenate two automatons
    private static Automaton concatenate(Automaton a1, Automaton a2) {
        // Add epsilon transition from end state of a1 to start state of a2
        a1.addTransition(new Transition(a1.getAcceptingState(), a2.getStartState(), Transition.EPSILON));
        // The start state is the start of a1, the accepting state is the accepting state of a2
        Automaton automaton = new Automaton(a1.getStartState(), a2.getAcceptingState());
        automaton.addStates(a1.getStates());
        automaton.addStates(a2.getStates());
        automaton.addTransitions(a1.getTransitions());
        automaton.addTransitions(a2.getTransitions());
        automaton.getAcceptingStates().clear();
        automaton.addAcceptingState(a2.getAcceptingState());
        return automaton;
    }

    // Alternate between two automatons
    private static Automaton alternate(Automaton a1, Automaton a2) {
        State start = new State();
        State end = new State();
        Automaton automaton = new Automaton(start, end);

        // Add epsilon transitions from new start state to start states of a1 and a2
        automaton.addTransition(new Transition(start, a1.getStartState(), Transition.EPSILON));
        automaton.addTransition(new Transition(start, a2.getStartState(), Transition.EPSILON));

        // Add epsilon transitions from accepting states of a1 and a2 to new accepting state
        for (State acceptState : a1.getAcceptingStates()) {
            automaton.addTransition(new Transition(acceptState, end, Transition.EPSILON));
        }
        for (State acceptState : a2.getAcceptingStates()) {
            automaton.addTransition(new Transition(acceptState, end, Transition.EPSILON));
        }

        automaton.addStates(a1.getStates());
        automaton.addStates(a2.getStates());
        automaton.addTransitions(a1.getTransitions());
        automaton.addTransitions(a2.getTransitions());
        automaton.getAcceptingStates().clear();
        automaton.addAcceptingState(end);
        return automaton;
    }

    // Kleene star of an automaton
    private static Automaton kleeneStar(Automaton a) {
        State start = new State();
        State end = new State();
        Automaton automaton = new Automaton(start, end);

        // Add epsilon transitions for the Kleene star construction
        automaton.addTransition(new Transition(start, end, Transition.EPSILON));
        automaton.addTransition(new Transition(start, a.getStartState(), Transition.EPSILON));
        for (State acceptState : a.getAcceptingStates()) {
            automaton.addTransition(new Transition(acceptState, end, Transition.EPSILON));
            automaton.addTransition(new Transition(acceptState, a.getStartState(), Transition.EPSILON));
        }

        automaton.addStates(a.getStates());
        automaton.addTransitions(a.getTransitions());
        automaton.getAcceptingStates().clear();
        automaton.addAcceptingState(end);
        return automaton;
    }
}
