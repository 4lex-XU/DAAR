import java.util.*;

public class DfaMinimizer {

    public static Automaton minimize(Automaton dfa) {
        // Étape 1 : Partition initiale
        List<Set<State>> partitions = new ArrayList<>();
        Set<State> acceptingStates = new HashSet<>(dfa.getAcceptingStates());
        Set<State> nonAcceptingStates = new HashSet<>(dfa.getStates());
        nonAcceptingStates.removeAll(acceptingStates);

        if (!acceptingStates.isEmpty()) partitions.add(acceptingStates);
        if (!nonAcceptingStates.isEmpty()) partitions.add(nonAcceptingStates);

        // Ensemble des symboles (alphabets) utilisés dans le DFA
        Set<Character> symbols = dfa.getSymbols();

        boolean changed = true;
        while (changed) {
            changed = false;
            List<Set<State>> newPartitions = new ArrayList<>();
            for (Set<State> group : partitions) {
                Map<Map<Character, Integer>, Set<State>> groupedStates = new HashMap<>();
                for (State state : group) {
                    Map<Character, Integer> transitionsMap = new HashMap<>();
                    for (char symbol : symbols) {
                        State nextState = getNextState(dfa, state, symbol);
                        int partitionIndex = getPartitionIndex(partitions, nextState);
                        transitionsMap.put(symbol, partitionIndex);
                    }
                    groupedStates.computeIfAbsent(transitionsMap, k -> new HashSet<>()).add(state);
                }
                newPartitions.addAll(groupedStates.values());
                if (groupedStates.size() > 1) {
                    changed = true;
                }
            }
            partitions = newPartitions;
        }

        // Étape 4 : Construction du DFA minimisé
        Map<State, State> stateMapping = new HashMap<>();
        Automaton minimizedDfa = new Automaton();
        for (Set<State> group : partitions) {
            State representative = group.iterator().next();
            State newState = new State();
            minimizedDfa.addState(newState);
            if (group.contains(dfa.getStartState())) {
                minimizedDfa.setStartState(newState);
            }
            if (!Collections.disjoint(group, dfa.getAcceptingStates())) {
                minimizedDfa.addAcceptingState(newState);
            }
            for (State oldState : group) {
                stateMapping.put(oldState, newState);
            }
        }

        // Ajouter les transitions
        for (State oldState : dfa.getStates()) {
            State newFromState = stateMapping.get(oldState);
            for (Transition t : dfa.getTransitionsFromState(oldState)) {
                State oldToState = t.getToState();
                State newToState = stateMapping.get(oldToState);
                if (newToState != null) {
                    minimizedDfa.addTransition(new Transition(newFromState, newToState, t.getSymbol()));
                }
            }
        }

        return minimizedDfa;
    }

    private static int getPartitionIndex(List<Set<State>> partitions, State state) {
        if (state == null) return -1;
        for (int i = 0; i < partitions.size(); i++) {
            if (partitions.get(i).contains(state)) {
                return i;
            }
        }
        return -1;
    }

    private static State getNextState(Automaton dfa, State state, char symbol) {
        for (Transition t : dfa.getTransitionsFromState(state)) {
            if (t.getSymbol() == symbol) {
                return t.getToState();
            }
        }
        return null;
    }
}
