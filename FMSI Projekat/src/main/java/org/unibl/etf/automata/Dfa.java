package org.unibl.etf.automata;

import org.unibl.etf.addition.Pair;
import org.unibl.etf.exceptions.DuplicateTransitionException;
import org.unibl.etf.interfaces.IMinimizable;
import org.unibl.etf.interfaces.IRegularLanguage;

import java.util.*;

public class Dfa implements IMinimizable,IRegularLanguage {
    private static final String PREFIX = "p";
    private String startState;
    private HashSet<String> states;
    private HashSet<String> finalStates;
    private HashSet<Character> alphabet;
    private HashMap<Pair<String,Character>, String> delta;

    public Dfa() {
        states = new HashSet<>();
        finalStates = new HashSet<>();
        alphabet = new HashSet<>();
        delta = new HashMap<>();
    }

    public Dfa(String startState) {
        this.startState = startState;
        states = new HashSet<>();
        finalStates = new HashSet<>();
        alphabet = new HashSet<>();
        delta = new HashMap<>();

        states.add(startState);
    }
    public Dfa(String startState,Character[] alphabet){
        this.startState = startState;
        states = new HashSet<>();
        finalStates = new HashSet<>();
        this.alphabet = new HashSet<>();
        for(Character symbol : alphabet){
            this.alphabet.add(symbol);
        }
        delta = new HashMap<>();
        states.add(startState);
    }

    private interface FunctionParameter{
        boolean isGood(HashSet<String>finals1,HashSet<String>finals2,String s1,String s2);
    }
    private void containsCheck(String currentState, Character symbol, String nextState){
        if (delta.containsKey(new Pair<>(currentState, symbol))) {
            throw new DuplicateTransitionException("Postojeca tranzicija!");
        }
        delta.put(new Pair<>(currentState, symbol), nextState);
        if (!states.contains(nextState)) {
            states.add(nextState);
            states.add(currentState);
        }
    }
    /*
     A method for adding a transition.
     Enables the addition of transitions, while checking whether the transition already exists, if it exists, it is thrown
     an exception for which the auxiliary function containsCheck is used, as well as for filling in at the same time
     the alphabet and states of automata.
     */
    public void addTransition(String currentState, Character symbol, String nextState) {
        this.containsCheck(currentState,symbol,nextState);
        if(!alphabet.contains(symbol)){
            alphabet.add(symbol);
        }
    }
    /*
     A method to add a transition.
     A method that also allows adding transitions in case the alphabet is already specified in the occasion
     automata constructions.
     */
    public void addTransitionVol_2(String currentState, Character symbol, String nextState){
        this.containsCheck(currentState,symbol,nextState);
        if(!alphabet.contains(symbol)){
            throw new IllegalArgumentException("Nepostojeci simbol u alfabetu!");
        }
    }

    /*
     Method for adding final states.
     Addition of final states, whereby if the state does not exist in the state set of the automaton, an exception is thrown.
     */
    public void addFinalStates(String state) {
        if (!states.contains(state)) {
            throw new IllegalArgumentException("Nepostojece stanje!\n");
        }
        finalStates.add(state);
    }

    /*
     A method for checking whether a string belongs to a language.
     A method that checks whether the automaton accepts the corresponding word at the input. It takes an input string as a parameter.
     We pass character by character through the input string and for each character we determine the transition of that
     we update the current state in which it is located, which in the beginning is exactly the initial state.
     The method checks whether the input string contains non-alphabetic symbols.
     If the state we reached belongs to the set of final states, the word is accepted.
     */
    public boolean accept(String input) {

        String currentState = startState;
        for(Character symbol : input.toCharArray()) {
            if (!delta.containsKey(new Pair<>(currentState,symbol))) {
                throw new IllegalArgumentException("Unesena rijec sadrzi simbole koji ne pripadaju alfabetu!\n");
            }
            currentState = delta.get(new Pair<>(currentState,symbol));
        }
        if (finalStates.contains(currentState)) {
            return true;
        }
        else {
            //System.out.println("Automat je zavrsio u stanju " + currentState);
            return false;
        }
    }

    /*
     A helper method that discards all unreachable states to the automaton object on which the method is called.
     The idea is to have a set of attainable states that we initially fill with initial states and states to which we reach
     for all symbols comes directly from the initial one. By iterating through the current set of reachable states at each step
     we fill it with the new states we have reached. If in some iteration the set is not expanded by one
     the algorithm ends there with a new state from the set of states. All states that are not in that set are unreachable states,
     and we remove them from the set of states and update the set of final states.
     */
    private void inaccessibleStates() {
        HashSet<String> accesibleStates = new HashSet<>();
        for(Character symb : alphabet)
        {
            accesibleStates.add(startState);
            if(delta.get(new Pair<>(startState,symb))!=null)
                accesibleStates.add(delta.get(new Pair<>(startState,symb)));
        }
        int count = 0;
        do {
            count = accesibleStates.size();
            HashSet<String> temp = new HashSet<>();
            temp.addAll(accesibleStates);
            for (String state : temp) {
                for(char symb : alphabet) {
                    if(delta.get(new Pair<>(state,symb)) != null)
                        accesibleStates.add(delta.get(new Pair<>(state,symb)));
                }
            }
        } while (count != accesibleStates.size());
        HashSet<String> inaccessibleStates = new HashSet<>();
        inaccessibleStates.addAll(states);
        inaccessibleStates.removeAll(accesibleStates);
        states = accesibleStates;
        for (String k : inaccessibleStates) {
            if(finalStates.contains(k))
                finalStates.remove(k);
            for (Character symb : alphabet) {
                delta.remove(new Pair<>(k, symb));
            }
        }
    }
    /*
    A helper method that returns all states that are not final
     */
    public HashSet<String> getNonFinalStates() {
        HashSet<String> nonFinalStates = new HashSet<String>();
        nonFinalStates.addAll(states);
        nonFinalStates.removeAll(finalStates);
        return nonFinalStates;
    }
    /*
     This method is responsible for returning a HashSet of all pairs of states where there is exactly one final state,
     and that is the initial set that we will use in the minimization procedure.
     */
    private HashSet<Pair<String, String>> getCorrectPairsOfStates() {
        HashSet<Pair<String, String>> correctPairsOfStates = new HashSet<>();
        HashSet<String> nonFinalStates = new HashSet<String>();
        nonFinalStates.addAll(this.getNonFinalStates());
        for (String nfs : nonFinalStates)
            for (String fs : finalStates)
                correctPairsOfStates.add(new Pair<>(nfs, fs));
        return correctPairsOfStates;
    }
    /*
     A method that creates and returns a HashSet of all pairs of states where either both are final or both are non-final
     and we achieve that through this block of code.
     When forming pairs, we do not form pairs of type (q0,q0), which is enabled
     in the condition of the inner for loop. Additionally, the condition checks that if there is a pair (q0,q1), it does not have to be added
     shape pair (q1,q0)
     */
    private HashSet<Pair<String, String>> getIncorrectPairsOfStates() {
        HashSet<Pair<String, String>> incorrectPairsOfStates = new HashSet<>();
        HashSet<String> nonFinalStates = new HashSet<String>();
        nonFinalStates.addAll(this.getNonFinalStates());
        for (String fs : finalStates)
            for (String fs1 : finalStates)
                if (!fs.equals(fs1) && !incorrectPairsOfStates.contains(new Pair<>(fs, fs1)) &&
                        !incorrectPairsOfStates.contains(new Pair<>(fs1, fs)))
                    incorrectPairsOfStates.add(new Pair<>(fs, fs1));
        for (String nfs : nonFinalStates)
            for (String nfs1 : nonFinalStates)
                if (!nfs.equals(nfs1) && !incorrectPairsOfStates.contains(new Pair<>(nfs, nfs1)) &&
                        !incorrectPairsOfStates.contains(new Pair<>(nfs1, nfs)))
                    incorrectPairsOfStates.add(new Pair<>(nfs, nfs1));
        return incorrectPairsOfStates;
    }
    private void printEquivalanceClasses(HashSet<HashSet<String>> equivalentClasses) {
        System.out.println("Klase ekvivalencije: ");
        for (var klasa : equivalentClasses) {
            System.out.print("{ ");
            for (var el : klasa)
                System.out.print(el + " ");
            System.out.println("}");
        }
    }
    /*
     A method that enables updating the parameters of our machine. The method receives equivalence classes as a parameter
     For each equivalence class we can determine the intersection with the final states to determine which equivalence class is
     now the state of the minimized automaton which is final. Naming of states is done with the help of counters, because they will be minimized
     atomata each class be a new state. If some class of equivalence contains the initial state, then that class
     represents the initial state of the minimized automaton.
     After that, we take one representative for each equivalence class and observe the transitions for that state.
     The state into which a specific symbol from the class representative is entered will serve us to form a new transition.
     numOfEquivalentCLass is used for naming newly created states (equivalence class), while br is used for naming states
     to which we move (equivalence classes), if that class is already named, we do not increase the counter.
     We form a new delta function by adding the pair (name of the current class, symbol) -> the class we are moving to.
     Finally, all attributes of the current automaton are updated.
     */
    private void changeAtributes(HashSet<HashSet<String>> equivalentClasses) {
        String changedStartState = "";
        HashSet<String> changedStates = new HashSet<>();
        HashSet<String> changedFinalStates = new HashSet<>();
        HashMap<Pair<String, Character>, String> changedDelta = new HashMap<>();
        int numOfEquivalentCLass = 0;
        for (HashSet<String> equivalenceClass : equivalentClasses) {
            Set<String> tempClasses = new HashSet<>();
            tempClasses.addAll(equivalenceClass);
            tempClasses.retainAll(this.finalStates);
            String newName = Integer.toString(numOfEquivalentCLass);
            changedStates.add(newName);
            if (tempClasses.size() > 0)
                changedFinalStates.add(newName);
            if (equivalenceClass.contains(this.startState))
                changedStartState = newName;
            numOfEquivalentCLass++;
        }
        numOfEquivalentCLass = 0;
        for (var equivalenceClass : equivalentClasses) {
            String mainElement = equivalenceClass.iterator().next();
            for (Character symbol : alphabet) {
                if(delta.get(new Pair<>(mainElement, symbol) ) != null) {
                    String changedState = delta.get(new Pair<>(mainElement, symbol));
                    int br = 0;
                    for (var equivalnceClass2 : equivalentClasses) {
                        if (equivalnceClass2.contains(changedState))
                            break;
                        br++;
                    }
                    changedDelta.put(new Pair<>(Integer.toString(numOfEquivalentCLass), symbol), Integer.toString(br));
                }
            }

            numOfEquivalentCLass++;
        }
        this.states = changedStates;
        this.delta = changedDelta;
        this.finalStates = changedFinalStates;
        this.startState = changedStartState;
    }
    /*
     Implementation of the IMinimizable Interface.
     A method that minimizes automata using previously defined functionalities. Rejection is done first
     unattainable states, after which equivalence classes are created. The idea is to observe all pairs at the beginning
     states that have exactly one final and all for which it does not apply. We initially initialize the set of pairs with a set of states
     where one is final, and in each iteration of the do while loop we go through other pairs of states that are not in the final set
     and we expand that set with that pair for which there is at least one symbol from the alphabet for which that pair of states transitions
     to a couple that is already in that set. The algorithm stops when that set is not expanded in the iteration.
     As part of the method, a transition is determined for the elements of a pair and it is checked whether that pair belongs to the set, if it does
     we remove the pair from the set we are checking and add it to the final set.
     The procedure for creating equivalence classes is based on the fact that from the pairs of states that remain
     we form special equivalence classes and then check the connection of those classes we have formed.
     If we have states of the same type in some classes, that is, for the same symbols, they move to the same classes
     we merge those classes into one, and we remove the individual ones. For all states that are not contained in any class, we form
     unit classes containing only those states.
     */
    @Override
    public void minimize() {
        this.inaccessibleStates(); /* rejection of unattainable states */
        HashSet<Pair<String, String>> correctPairsOfStates = new HashSet<>();
        HashSet<Pair<String, String>> incorrectPairsOfStates = new HashSet<>();
        // we form all pairs of states in which exactly one is final
        correctPairsOfStates.addAll(this.getCorrectPairsOfStates());
        // we form other pairs
        incorrectPairsOfStates.addAll(this.getIncorrectPairsOfStates());
        HashSet<Pair<String, String>> incorrectTemp = new HashSet<>();
        incorrectTemp.addAll(incorrectPairsOfStates);
        int count = 0;
        do {
            count = correctPairsOfStates.size();
            for (Pair<String, String> p : incorrectTemp)
                for (Character c : alphabet) {
                    if (delta.get(new Pair<>(p.first, c)) != null && delta.get(new Pair<>(p.second, c)) != null) {
                        Pair<String, String> temp = new Pair<>(delta.get(new Pair<>(p.first, c)),
                                delta.get(new Pair<>(p.second, c)));
                        Pair<String, String> temp1 = new Pair<>(delta.get(new Pair<>(p.second, c)),
                                delta.get(new Pair<>(p.first, c)));
                        if (correctPairsOfStates.contains(temp) || correctPairsOfStates.contains(temp1)) {
                            incorrectPairsOfStates.remove(new Pair<>(p.first, p.second));
                            correctPairsOfStates.add(new Pair<>(p.first, p.second));
                        }
                    }
                }
        } while (count != correctPairsOfStates.size());

        HashSet<HashSet<String>> equivalentClasses = new HashSet<>();
        for (var par : incorrectPairsOfStates) {
            HashSet<String> equivalentClass = new HashSet<>();
            equivalentClass.add(par.first);
            equivalentClass.add(par.second);
            equivalentClasses.add(equivalentClass);
        }
        HashSet<HashSet<String>> temp = new HashSet<>();
        temp.addAll(equivalentClasses);
        for (var klasa : temp) {
            for (var klasa1 : temp) {
                if (!klasa.equals(klasa1)) {
                    for (String state : klasa1) {
                        if (klasa.contains(state)) {
                            klasa.addAll(klasa1);
                            equivalentClasses.remove(klasa1);
                        }
                    }
                }
            }
        }
        boolean contains = true;
        for (String state : states) {
            for (var equivalentClass : equivalentClasses)
                if (equivalentClass.contains(state))
                    contains = false;

            if (contains) {
                HashSet<String> unitClass = new HashSet<>();
                unitClass.add(state);
                equivalentClasses.add(unitClass);
            }
            contains = true;
        }
        //this.printEquivalanceClasses(equivalentClasses);
        this.changeAtributes(equivalentClasses);
    }
    /*
     Implementation of the IRegularLanguage Interface.
     The method that performs the conversion to EpsilonNfa, although each Dfa is also EpsilonNfa, we just form a set of states
     into which we pass using the delta function Dfa, so that set in this case contains only one state.
     The other elements are identical and we only set them in the appropriate way.
     */
    @Override
    public EpsilonNfa toENfa(){
        EpsilonNfa epsilonNfa = new EpsilonNfa(this.startState);
        epsilonNfa.getAlphabet().addAll(this.getAlphabet());
        epsilonNfa.getStates().addAll(this.getStates());
        epsilonNfa.getFinalStates().addAll(this.getFinalStates());

        for(var transition : this.getDelta().entrySet()){
            HashSet<String> transitionStates = new HashSet<>();
            transitionStates.add(transition.getValue());
            epsilonNfa.getDelta().put(new Pair<>(transition.getKey().first,transition.getKey().second), transitionStates);
        }
        return epsilonNfa;
    }

    /*
     Implementation of the IRegularLanguage Interface.
     A method that returns the length of the shortest word.
     The most common word is detected by using BFS traversal, the idea is that stateLevel in HashMap remembers the level
     for each state, that level represents the number of transitions to that state. Let's use a row as an auxiliary structure
     for the implementation of the BFS algorithm. If the initial state is final, the shortest word is of length zero, in other cases
     we visit state by state and update the level for that state as the level of the previous one increased by one transition.
     */
    @Override
    public int minimumLengthWord(){
        Queue<String> visited = new LinkedList<>();
        visited.add(this.startState);
        HashMap<String,Integer> stateLevel = new HashMap<>();
        stateLevel.put(this.startState,0);
        if(this.finalStates.contains(this.startState))
            return 0;
        while(!visited.isEmpty()) {
            String state = visited.remove();
            for (var c : this.alphabet) {
                String transition = this.getDelta().get(new Pair<>(state, c));
                if(!stateLevel.containsKey(transition)){
                    visited.add(transition);
                    stateLevel.put(transition,stateLevel.get(state) + 1);
                    if(this.finalStates.contains(transition)){
                        return stateLevel.get(transition);
                    }
                }
            }
        }
        return -1;
    }

    /*
     Implementation of the IRegularLanguage Interface.
     The method that performs the conversion to Dfa.
     */
    @Override
    public Dfa toDfa(){
        return this;
    }

    /*
     Implementation of the IRegularLanguage Interface.
     A method that checks the equality of two Regular Languages
     The equality of two automata can be easily checked by observing pairs of states. At the beginning of the algorithm
     we add the initial states of both automata and go through the BFS again, for each pair we visit and for each symbol
     let's determine a pair of states to which we pass, if these states are of the same nature or both final or both non-final
     we continue the algorithm, if there are no more newly created pairs and we did not detect a deviation from equivalence
     two automata then they are equivalent, otherwise we return false.
     */
    @Override
    public boolean equality(IRegularLanguage other){
        Dfa otherDfa = (Dfa)other.toDfa();
        if(!this.alphabet.equals(otherDfa.getAlphabet()))
            return false;
        HashSet<Pair<String,String>> pairs = new HashSet<>();
        pairs.add(new Pair<>(this.startState,otherDfa.getStartState()));
        Queue<Pair<String,String>> visited = new LinkedList<>();
        visited.add(new Pair<>(this.startState,otherDfa.getStartState()));
        while(!visited.isEmpty()){
            Pair<String,String> pair = visited.remove();
            for(var symbol : this.alphabet){
                Pair<String,String> transition = new Pair<>(this.delta.get(new Pair<>(pair.first,symbol)),
                        otherDfa.getDelta().get(new Pair<>(pair.second,symbol))); // let's move on to a new pair
                if(this.finalStates.contains(transition.first) && otherDfa.getFinalStates().contains(transition.second)
                        || this.getNonFinalStates().contains(transition.first) && otherDfa.getNonFinalStates().contains(transition.second)){
                    if(!pairs.contains(transition)){
                        pairs.add(transition);
                        visited.add(transition);
                    }
                }else{
                    return false;
                }
            }
        }
        return true;
    }

    /*
     A common method that performs the formation of a new Dfa that can be obtained by union, intersection or difference of automata.
     The algorithm followed by the method is based on the iterative formation of state pairs of a new automaton.
     The method receives an RJ representation and a function parameter that will specify the appropriate check
     pairs depending on whether they belong to the final states of the new automaton.
     With the help of the BFS algorithm, we cut pairs of states, until there are no more formed pairs, pairs are formed from
     states of both automata on which we perform the operation. We name each pair, because it represents one state in a new one
     machine. If we have found a new pair, name it, go around and add a transition to the delta function.
     If the pair that we have reached already exists, we get the state in the new automaton that it represents even then
     add the corresponding transition.
     The final states are determined based on the function parameter, because the procedure for union, intersection, difference is the same
     except for final states.
     */
    private IRegularLanguage evaluate(IRegularLanguage other,FunctionParameter f){
        Dfa otherDfa = (Dfa)other.toDfa();
        if(!this.alphabet.equals(otherDfa.getAlphabet()))
            throw new IllegalArgumentException();
        if(other == null){
            throw new IllegalArgumentException();
        }
        String namePrefix = "p";
        int nameEditor = 0;
        Dfa result = new Dfa();
        String startsState = PREFIX + nameEditor++;
        Queue<Pair<String,String>> visitedPairs = new LinkedList<>();
        HashMap<Pair<String,String>,String> formedPairs = new HashMap<>();
        visitedPairs.add(new Pair<>(this.startState,otherDfa.getStartState()));
        formedPairs.put(new Pair<>(this.startState,otherDfa.getStartState()),startsState);
        result.setStartState(startsState);
        result.getStates().add(startsState);
        if(f.isGood(this.finalStates,otherDfa.finalStates,this.startState,otherDfa.startState))
            result.addFinalStates(startsState);
        while(!visitedPairs.isEmpty()) {
            Pair<String, String> pair = visitedPairs.remove();
            for (var symbol : this.alphabet) {
                Pair<String, String> transition = new Pair<>(this.delta.get(new Pair<>(pair.first, symbol)),
                        otherDfa.getDelta().get(new Pair<>(pair.second, symbol)));
                String name = "";
                if (!formedPairs.containsKey(transition)) {
                    name = PREFIX + nameEditor++;
                    formedPairs.put(transition,name);
                    visitedPairs.add(transition);
                } else
                    name = formedPairs.get(transition); // otherwise we get the name in the new automaton for that transition

                result.addTransition(formedPairs.get(pair), symbol, name); // we form a new delta, we get a name for the current one, and for the given symbol the transition to the newly formed one
                if(f.isGood(this.finalStates,otherDfa.finalStates,transition.first,transition.second))
                    result.addFinalStates(name);
            }
        }
        return result;
    }
    /*
    Implementation of the IRregular Language Interface.
    A method that determines the Union of two Regular Languages using the evaluate method.
     */
    @Override
    public IRegularLanguage union(IRegularLanguage other) {
        return this.evaluate(other, new FunctionParameter() {
            @Override
            public boolean isGood(HashSet<String> finals1, HashSet<String> finals2, String s1, String s2) {
                return finals1.contains(s1) || finals2.contains(s2);
            }
        });
    }
    /*
     Implementation of the IRegularLanguage Interface.
     A method that determines the Intersection of two Regular Languages using the evaluate method.
     */
    @Override
    public IRegularLanguage intersection(IRegularLanguage other) {
        return this.evaluate(other, new FunctionParameter() {
            @Override
            public boolean isGood(HashSet<String> finals1, HashSet<String> finals2, String s1, String s2) {
                return finals1.contains(s1) && finals2.contains(s2);
            }
        });
    }
    /*
    Implementation of the IRegularLanguage Interface.
    A method that determines the Difference between two Regular Languages using the evaluate method.
    */
    @Override
    public IRegularLanguage difference(IRegularLanguage other) {
        return this.evaluate(other, new FunctionParameter() {
            @Override
            public boolean isGood(HashSet<String> finals1, HashSet<String> finals2, String s1, String s2) {
                return finals1.contains(s1) && !finals2.contains(s2);
            }
        });
    }
    /*
     Implementation of the IRegularLanguage Interface.
     A method that performs the concatenation of two Regular Languages.
     We convert Dfa to EpsilonNfa and use the concatenation functionality defined in EpsilonNfa.
     As a result we return IRegularLanguage, a representation of Regular Language
     */
    @Override
    public IRegularLanguage concatenation(IRegularLanguage other){

        return this.toENfa().concatenation(other.toENfa()).toDfa();
    }
    /*
     Implementation of the IRegularLanguage Interface.
     A method that performs the concatenation of two Regular Languages.
     We convert Dfa to EpsilonNfa and use the functionality of the clinian star defined in EpsilonNfa.
     As a result we return IRegularLanguage, a representation of Regular Language
     */
    @Override
    public IRegularLanguage kleeneStar(){

        return this.toENfa().kleeneStar().toDfa();
    }

    /*
     /*
     Implementation of the IRegularLanguage Interface.
     A method that determines the complement of a given Regular Language
     The method "swaps" the final states so that all non-final states now become final
     */
    @Override
    public IRegularLanguage complement(){
        Dfa result = new Dfa(this.startState);
        result.setAplphabet(this.alphabet);
        result.setStates(this.states);
        result.setFinalStates(this.getNonFinalStates()); // now all those that were not are final
        result.getDelta().putAll(this.delta);
        return result;
    }
    /*
     Implementation of the IRegularLanguage Interface.
     A method that determines the length of the longest word accepted by the automaton.
     If the language is not final (we use the finality method), we return -1, otherwise
     we use the BFS algorithm that determines the levels of each state, taking into account that if we visit a state
     again, and it was a long way (with a greater number of transitions), we are updating the new level of that state
     */
    @Override
    public int maximumLengthWord(){
        if(!this.finality()) {
            System.out.println("Jezik nije konacan!");
            return -1;
        }
        Queue<String> visited = new LinkedList<>();
        visited.add(this.startState);
        HashMap<String,Integer> stateLevel = new HashMap<>();
        stateLevel.put(this.startState,0);
        while(!visited.isEmpty()) {
            String state = visited.remove();
            for (var c : this.alphabet) {
                String transition = this.getDelta().get(new Pair<>(state, c));
                if(!stateLevel.containsKey(transition)){
                    visited.add(transition);
                    stateLevel.put(transition,stateLevel.get(state) + 1);

                }else if (stateLevel.get(state) + 1 >= stateLevel.get(transition)) {
                    stateLevel.put(transition, stateLevel.get(state) + 1);
                }
            }
        }
        int max = 0;
        for(var el : stateLevel.entrySet()) {
            if(this.finalStates.contains(el.getKey()))
                if (el.getValue() > max)
                    max = el.getValue();
        }
        return max;
    }

    /*
     A helper class that represents a boolean primitive type vreper, which we pass as a parameter
     as a detection of the finitude of language.
     */
    private class StatusWrapper{
        public boolean status;

        public StatusWrapper(boolean status) {
            this.status = status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }
        public boolean getStatus(){
            return this.status;
        }

    }
    /*
     Implementation of the IRegularLanguage Interface
     A method that checks the finiteness of a language.
     */
    @Override
    public boolean finality()
    {
        HashSet<String> visited = new HashSet<>();
        Stack<String> cycleDetect = new Stack<>();
        StatusWrapper status = new StatusWrapper(true);
        this.finalityDfs(visited,cycleDetect,this.startState,status);
        return status.getStatus();
    }

    /*
     The idea of this algorithm is based on Dfs traversal of the state of the automaton, if we reach a state that we have already visited
     (if it is on the stack) we perform cycle detection. If the final state can somehow be reached from the cycle itself
     then the language is not finite. If the balance is not on the stack, we continue the tour. It is enough to check the way to the final
     states from at least one state that makes up the cycle. Checking the reachability to the final state is done by an assistant
     with the finalReachable method, which receives as an argument the state from which we check reachability.
     */
    private void finalityDfs(HashSet<String> visited,Stack<String> cycleDetect,String startState,StatusWrapper status)
    {
        visited.add(startState);
        cycleDetect.push(startState);
        for(var symbol:this.alphabet)
        {
            var nextState=this.delta.get(new Pair<>(startState,symbol));
            if(!visited.contains(nextState))
                finalityDfs(visited,cycleDetect,nextState,status);
            else if(cycleDetect.search(nextState)!=-1) {
                boolean finalReachable = this.finalReachable(nextState);
                if(finalReachable)
                    status.setStatus(false);
            }
        }
        cycleDetect.pop();
    }
    /*
     Helper method that receives the state and checks using the BFS procedure whether the final state can be reached from that state
     If we encounter a condition that has not been tried, we try it, and if we reach the final one, we stop the algorithm there
     */
    private boolean finalReachable(String currentState)
    {
        Queue<String> forVisiting = new LinkedList<>();
        HashSet<String> visitedStates = new HashSet<>();
        forVisiting.add(currentState);
        visitedStates.add(currentState);
        if(this.finalStates.contains(currentState))
            return true;
        while(!forVisiting.isEmpty())
        {
            String oldState=forVisiting.remove();
            for(var symbol:this.alphabet)
            {
                String newState=this.delta.get(new Pair<>(oldState,symbol));
                if(!visitedStates.contains(newState))
                {
                    forVisiting.add(newState);
                    visitedStates.add(newState);
                }
                if(this.finalStates.contains(newState))
                    return true;
            }
        }
        return false;
    }


    // setters and getters

    public String getStartState()
    {
        return startState;
    }
    public HashSet<String> getStates()
    {
        return states;
    }
    public HashSet<String> getFinalStates()
    {
        return finalStates;
    }
    public HashSet<Character> getAlphabet()
    {
        return alphabet;
    }
    public HashMap<Pair<String,Character>, String> getDelta()
    {
        return delta;
    }


    public void setStartState(String startState)
    {
        this.startState = startState;
    }
    public void setStates(HashSet<String> states)
    {
        this.states = states;
    }
    public void setFinalStates(HashSet<String> finalStates)
    {
        this.finalStates = finalStates;
    }
    public void setAplphabet(HashSet<Character> alphabet)
    {
        this.alphabet = alphabet;
    }
    public void SetDelta(HashMap<Pair<String,Character>, String> delta)
    {
        this.delta = delta;
    }
}
