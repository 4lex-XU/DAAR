import java.util.*;

public class NfaToDfaConverter {

    public static Automaton convertToDFA(Automaton nfa) {
        Automaton dfa = new Automaton();
        Map<Set<State>, State> stateSetsToState = new HashMap<>();
        Queue<Set<State>> queue = new LinkedList<>();

        // Étape 1 : Calcul de l'épsilon-fermeture de l'état initial du NFA
        Set<State> startClosure = epsilonClosure(nfa, nfa.getStartState());
        State dfaStartState = new State();
        dfa.setStartState(dfaStartState);
        stateSetsToState.put(startClosure, dfaStartState);
        queue.add(startClosure);
        dfa.addState(dfaStartState);

        // Ensemble des symboles (alphabets) utilisés dans le NFA (excluant epsilon)
        Set<Character> symbols = nfa.getSymbols();

        // Étape 2 : Boucle principale de déterminisation
        while (!queue.isEmpty()) {
            Set<State> currentSet = queue.poll();
            State currentDfaState = stateSetsToState.get(currentSet);

            for (char symbol : symbols) {
                // Ignorer les transitions epsilon
                if (symbol == Transition.EPSILON) continue;

                // Calculer les états atteints par le symbole à partir de l'épsilon-fermeture
                Set<State> moveResult = move(nfa, currentSet, symbol);
                Set<State> closure = epsilonClosure(nfa, moveResult);

                if (!closure.isEmpty()) {
                    State dfaState;
                    if (!stateSetsToState.containsKey(closure)) {
                        dfaState = new State();
                        stateSetsToState.put(closure, dfaState);
                        queue.add(closure);
                        dfa.addState(dfaState);

                        // Si l'un des états du NFA est acceptant, marquer l'état DFA comme acceptant
                        for (State nfaState : closure) {
                            if (nfaState.equals(nfa.getAcceptingState())) {
                                dfa.addAcceptingState(dfaState);
                                break;
                            }
                        }
                    } else {
                        dfaState = stateSetsToState.get(closure);
                    }
                    // Ajouter la transition au DFA
                    dfa.addTransition(new Transition(currentDfaState, dfaState, symbol));
                }
            }
        }

        return dfa;
    }

    // Méthode pour calculer l'épsilon-fermeture d'un état
    private static Set<State> epsilonClosure(Automaton automaton, State state) {
        Set<State> closure = new HashSet<>();
        Stack<State> stack = new Stack<>();
        closure.add(state);
        stack.push(state);

        while (!stack.isEmpty()) {
            State s = stack.pop();
            for (Transition t : automaton.getTransitionsFromState(s)) {
                if (t.getSymbol() == Transition.EPSILON && !closure.contains(t.getToState())) {
                    closure.add(t.getToState());
                    stack.push(t.getToState());
                }
            }
        }

        return closure;
    }

    // Méthode pour calculer l'épsilon-fermeture d'un ensemble d'états
    private static Set<State> epsilonClosure(Automaton automaton, Set<State> states) {
        Set<State> closure = new HashSet<>();
        for (State s : states) {
            closure.addAll(epsilonClosure(automaton, s));
        }
        return closure;
    }

    // Méthode pour calculer les états atteints à partir d'un ensemble d'états avec un symbole donné
    private static Set<State> move(Automaton automaton, Set<State> states, char symbol) {
        Set<State> result = new HashSet<>();
        for (State s : states) {
            for (Transition t : automaton.getTransitionsFromState(s)) {
                if (t.getSymbol() == symbol) {
                    result.add(t.getToState());
                }
            }
        }
        return result;
    }
}
