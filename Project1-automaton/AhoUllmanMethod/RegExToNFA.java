package AhoUllmanMethod;

public class RegExToNFA {
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
            // DOT represents any printable character except newline
            // Inclure les caractères ASCII printable
            for (char c = 32; c <= 126; c++) {
                if (c != '\n') {
                    automaton.addTransition(new Transition(start, end, c));
                }
            }

            // Inclure les caractères accents
            for (char c = 160; c <= 255; c++) {
                automaton.addTransition(new Transition(start, end, c));
            }

            for (char c = 256; c <= 383; c++) {
                automaton.addTransition(new Transition(start, end, c));
            }
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
