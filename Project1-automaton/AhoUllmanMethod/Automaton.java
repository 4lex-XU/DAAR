package AhoUllmanMethod;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

class State {
    private static int count = 0;
    private int id;

    public State() {
        this.id = count++;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return "S" + id;
    }

    public static void resetCount() {
        count = 0;
    }
}

class Transition {
    public static final char EPSILON = 'ε';
    private State from;
    private State to;
    private char symbol;

    public Transition(State from, State to, char symbol) {
        this.from = from;
        this.to = to;
        this.symbol = symbol;
    }

    public State getFromState() {
        return from;
    }

    public State getToState() {
        return to;
    }

    public char getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return from.toString() + " --" + symbol + "--> " + to.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Transition)) return false;
        Transition other = (Transition) obj;
        return from.equals(other.from) && to.equals(other.to) && symbol == other.symbol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, symbol);
    }
}

public class Automaton {
    private State startState;
    private State acceptingState;
    private Set<State> states;
    private Set<State> acceptingStates;
    private Set<Transition> transitions;
    private Map<State, Set<Transition>> transitionsFromState;

    private Map<State, Map<Character, State>> optimizedTransitionMap = new HashMap<>();

    public Automaton() {
        this.states = new HashSet<>();
        this.acceptingStates = new HashSet<>();
        this.transitions = new HashSet<>();
        this.transitionsFromState = new HashMap<>();
    }

    public Automaton(State startState, State acceptingState) {
        this();
        this.startState = startState;
        this.acceptingState = acceptingState;
        this.states.add(startState);
        this.states.add(acceptingState);
        this.acceptingStates.add(acceptingState);
    }

    public State getStartState() { return startState; }

    public void setStartState(State startState) {
        this.startState = startState;
        states.add(startState);
    }

    public State getAcceptingState() { return acceptingState; }

    public void setAcceptingState(State acceptingState) {
        this.acceptingState = acceptingState;
        acceptingStates.add(acceptingState);
        states.add(acceptingState);
    }

    public void addAcceptingState(State state) {
        acceptingStates.add(state);
        states.add(state);
    }

    public Set<State> getAcceptingStates() { return acceptingStates; }

    public Set<State> getStates() { return states; }

    public Set<Transition> getTransitions() { return transitions; }

    public void addState(State state) { states.add(state); }

    public void addStates(Set<State> states) { this.states.addAll(states); }

    public void addTransition(Transition transition) {
        transitions.add(transition);
        states.add(transition.getFromState());
        states.add(transition.getToState());
        transitionsFromState.computeIfAbsent(transition.getFromState(), k -> new HashSet<>()).add(transition);
    }

    public void addTransitions(Set<Transition> transitions) {
        for (Transition t : transitions) {
            addTransition(t);
        }
    }

    public Set<Transition> getTransitionsFromState(State state) {
        return transitionsFromState.getOrDefault(state, new HashSet<>());
    }

    public Set<Character> getSymbols() {
        Set<Character> symbols = new HashSet<>();
        for (Transition t : transitions) {
            if (t.getSymbol() != Transition.EPSILON) {
                symbols.add(t.getSymbol());
            }
        }
        return symbols;
    }

    /**
     * Construire une map optimisée pour les transitions.
     * Cette map permet un accès rapide à l'état suivant en fonction de l'état actuel et du symbole.
     */
    public void buildOptimizedTransitionMap() {
        for (State state : states) {
            Set<Transition> transitionsSet = transitionsFromState.get(state);
            if (transitionsSet != null) {
                Map<Character, State> symbolToState = new HashMap<>();
                for (Transition t : transitionsSet) {
                    symbolToState.put(t.getSymbol(), t.getToState());
                }
                optimizedTransitionMap.put(state, symbolToState);
            }
        }
    }

    public List<Integer> searchOptimized(String text) {
        List<Integer> matchPositions = new ArrayList<>();
        int textLength = text.length();

        for (int i = 0; i < textLength; i++) {
            State currentState = startState;
            int j = i;

            while (j < textLength) {
                char currentChar = text.charAt(j);
                Map<Character, State> transitions = optimizedTransitionMap.get(currentState);

                if (transitions != null && transitions.containsKey(currentChar)) {
                    currentState = transitions.get(currentChar);

                    if (acceptingStates.contains(currentState)) {
                        matchPositions.add(i);
                        break;
                    }

                    j++;
                } else {
                    break;
                }
            }
        }

        return matchPositions;
    }

    /**
     * Affiche les détails de l'automate.
     */
    public void display() {
        System.out.println("Automaton:");
        System.out.println("Start State: " + startState.toString());
        System.out.println("Accepting States: " + acceptingStates.toString());
        System.out.println("Transitions:");
        for (Transition t : transitions) {
            System.out.println("  " + t.toString());
        }
    }
}