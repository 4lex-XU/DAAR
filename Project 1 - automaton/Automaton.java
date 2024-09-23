import java.util.Set;
import java.util.HashSet;

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

  public String toString() {
    return from.toString() + " --" + symbol + "--> " + to.toString();
  }
}

class Automaton {
  private State startState;
  private State acceptingState;
  private Set<State> states;
  private Set<Transition> transitions;

  public Automaton(State startState, State acceptingState) {
    this.startState = startState;
    this.acceptingState = acceptingState;
    this.states = new HashSet<>();
    this.transitions = new HashSet<>();
    this.states.add(startState);
    this.states.add(acceptingState);
  }

  public State getStartState() {
    return startState;
  }

  public State getAcceptingState() {
    return acceptingState;
  }

  public Set<State> getStates() {
    return states;
  }

  public Set<Transition> getTransitions() {
    return transitions;
  }

  public void addState(State state) {
    states.add(state);
  }

  public void addStates(Set<State> states) {
    this.states.addAll(states);
  }

  public void addTransition(Transition transition) {
    transitions.add(transition);
    states.add(transition.getFromState());
    states.add(transition.getToState());
  }

  public void addTransitions(Set<Transition> transitions) {
    for (Transition t : transitions) {
      addTransition(t);
    }
  }

  public void display() {
    System.out.println("Automaton:");
    System.out.println("Start State: " + startState.toString());
    System.out.println("Accepting State: " + acceptingState.toString());
    System.out.println("Transitions:");
    for (Transition t : transitions) {
      System.out.println("  " + t.toString());
    }
  }
}
