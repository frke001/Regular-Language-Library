package org.unibl.etf.automata;

import java.util.*;
import org.unibl.etf.addition.*;
import org.unibl.etf.exceptions.DuplicateTransitionException;
import org.unibl.etf.interfaces.IRegularLanguage;
import org.unibl.etf.regularExpression.RegularExpression;

public class EpsilonNfa implements IRegularLanguage {
    private static final String PREFIX = "p";
    public static char EPSILON = '&';
    private HashSet<Character> alphabet;
    private HashSet<String> states;
    private HashSet<String> finalStates;
    private String startState;
    private HashMap<Pair<String,Character>, HashSet<String>> delta; // for state and input delta returns a set of states

    public EpsilonNfa(){
        this.alphabet = new HashSet<>();
        this.states = new HashSet<>();
        this.finalStates = new HashSet<>();
        this.delta = new HashMap<>();
    }
    public EpsilonNfa(String startState)
    {
        this.startState = startState;
        this.alphabet = new HashSet<>();
        this.states = new HashSet<>();
        this.finalStates = new HashSet<>();
        this.delta = new HashMap<>();

        states.add(startState);
        alphabet.add(EPSILON);
    }
    public EpsilonNfa(String startState,Character[] alphabet){
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

    private void containChech(String currentState, Character symbol,String nextStates[]){
        if (delta.containsKey(new Pair<>(currentState, symbol))) {
            throw new DuplicateTransitionException("Tranzicije za ovo stanje vec postoji!\n");
        }
        HashSet<String> next  = new HashSet<>();
        for (String state : nextStates) {
            next.add(state);
            if (!states.contains(state) || !states.contains(currentState)) {
                states.add(state);
                states.add(currentState);
            }
        }
        delta.put(new Pair<>(currentState, symbol), next);
    }
    /*
     A method for adding a transition.
     Enables the addition of transitions, while checking whether the transition already exists, if it exists, it is thrown
     an exception for which the auxiliary function containsCheck is used, as well as for filling in at the same time
     the alphabet and states of automata.
     */
    public void addTransition(String currentState, Character symbol,String nextStates[])
    {
        this.containChech(currentState,symbol,nextStates);
        if (!alphabet.contains(symbol))
        {
            alphabet.add(symbol);
        }
    }
    /*
     A method to add a transition.
     A method that also allows adding transitions in case the alphabet is already specified in the occasion
     automata constructions.
     */
    public void addTransitionVol_2(String currentState, Character symbol,String nextStates[])
    {
        this.containChech(currentState,symbol,nextStates);
        if (!alphabet.contains(symbol))
            throw new IllegalArgumentException("Nepostojeci simbol u alfabetu!");

    }
    /*
     Method for adding final states.
     Addition of final states, whereby if the state does not exist in the state set of the automaton, an exception is thrown.
     */
    public void addFinalStates(String state)
    {
        if (!states.contains(state))
        {
            throw new IllegalArgumentException("Nepostojece stanje!\n");
        }
        finalStates.add(state);
    }
    /*
     A method to determine the closure of the state it receives as an argument.
     If the condition does not exist, an exception is thrown. Otherwise, we add the current state to the Closure, like
     and all states to which it directly transitions with the Epsilon transition.
     The algorithm continues the formation of the Closure by checking the existence of all states from that set
     epsilon transitions to other states that are not in that set.
     The algorithm terminates when we have no more Closure expansions of the received state.
     */
    public HashSet<String> closure(String state) {
        if(!this.states.contains(state)){
            throw new IllegalArgumentException("Nepostojece stanje!\n");
        }
        HashSet<String> result = new HashSet<>();
        if(delta.get(new Pair<>(state, EPSILON)) != null) {
            result.addAll(delta.get(new Pair<>(state, EPSILON)));
        }
        result.add(state);
        HashSet<String> temp = new HashSet<>();
        temp.addAll(result);
        int size = 0;
        do
        {
            size = result.size();
            for (String s : result) {
                if(delta.get(new Pair<>(s, EPSILON)) != null) {
                    HashSet<String> epsTransition = new HashSet<>();
                    epsTransition.addAll(delta.get(new Pair<>(s, EPSILON)));
                    for (String e : epsTransition) {
                        temp.add(e);
                    }
                }
            }
            result.addAll(temp);

        } while (size != result.size());
        return result;
    }
    /*
     A method for checking whether a string belongs to a language.
     The algorithm follows the definition δ*(q,xσ)=U{δ(p,σ) ∣ p∈δ*(q,x)}
     The algorithm starts determining the Closure for the initial state, and we observe the transitions for each of those states,
     We determine the Closure of the obtained set of states, and now we continue the algorithm with the newly obtained set
     for the next symbol from the input string.
     */
    public boolean accept(String input)
    {
        HashSet<String> states = closure(startState);
        for (char symbol : input.toCharArray())
        {
            HashSet<String> setStates = new HashSet<>();
            for (String state : states)
            {
                if(delta.get(new Pair<>(state,symbol)) != null)
                    for (String s : delta.get(new Pair<>(state, symbol)))
                    {
                        setStates.add(s);
                    }
            }
            HashSet<String> newStates = new HashSet<>();
            for (String st : setStates)
            {
                HashSet<String> temp = closure(st);
                newStates.addAll(temp);
            }
            states = newStates;
        }
        for (String finalna : finalStates)
            if(states.contains(finalna))
                return true;
        return false;
    }
    /*
     Helper Method for updating attributes.
     The method receives as an argument the Dfa automaton that we form, as a set of sets of states that are iteratively formed in the conversion procedure
     in Dfa. For each set of states, we form new names in the new automaton, and if there is a final state in the set
     that set becomes final in the new Dfa machine. If the set contains the initial state,
     it represents the initial state of the new automaton.
     We form transitions in the new automaton in such a way that for each set we received, and for all elements of the set
     determine the transitions and Closures from that set of states to which we have transitioned in order to obtain some of the sets.
     pos is an auxiliary counter that serves us to form the name of the newly acquired state to which we are moving, whereas
     num represents the formation of the name of the state for which we determine the transition.
     The idea is to name each set (represents a new state in Dfa) and to form transitions that connect those sets.
     (that is, the state of the new machine).
     */
    private void changeAtributes(HashSet<HashSet<String>> dfaStateSet,Dfa dfa)
    {
        int br = 0;
        String name,prefix = "p";
        for(var skup : dfaStateSet) {
            name = PREFIX + br;
            dfa.getStates().add(name);
            HashSet<String> fin = new HashSet<>();
            fin.addAll(skup);
            fin.retainAll(this.finalStates);
            if (fin.size() > 0)
                dfa.getFinalStates().add(name);
            if(skup.contains(this.startState))
                dfa.setStartState(name);
            br++;
        }
        int num = 0;
        for(var set1 : dfaStateSet) {
            for (Character symbol : dfa.getAlphabet()) {
                int pos = 0;
                HashSet<String> temp = new HashSet<>();
                HashSet<String> closure = new HashSet<>();
                for (String state : set1) {
                    if (this.delta.get(new Pair<>(state, symbol)) != null)
                        temp.addAll(this.delta.get(new Pair<>(state, symbol)));
                }
                for (String s : temp)
                    closure.addAll(this.closure(s));
                for (var set2 : dfaStateSet) {
                    if (set2.equals(closure))
                        break;
                    pos++;
                }
                dfa.getDelta().put(new Pair<>(PREFIX + num, symbol), PREFIX + pos);
            }
            num++;
        }
    }
    /*
     Implementation of the IRegularLanguage Interface.
     A method that performs the conversion of EpsilonNfa to Dfa.
     We form a set of sets of states as per the algorithm iteratively, first a set with the initial state is formed.
     After that, we iteratively determine a new set to which we move from the current set that we process according to the algorithm
     to determine transitions for each element of the current set and from that set Closure.
     The algorithm continues until we have no more newly obtained state sets.
     */
    @Override
    public Dfa toDfa()
    {
        HashSet<HashSet<String>> dfaStateSet = new HashSet<>();
        HashSet<String> dfaStartState = this.closure(this.getStartState());
        dfaStateSet.add(dfaStartState);
        int br = 0;
        String prefix = "p";
        String startStateName = PREFIX + br;

        Dfa dfa = new Dfa(startStateName); // and adds initial to the set of states
        for(var symbol : this.alphabet)
            if(symbol!=EPSILON)
                dfa.getAlphabet().add(symbol);
        //dfa.setAplphabet(this.alphabet);
        //dfa.getAlphabet().remove(EPSILON);
        HashSet<HashSet<String>> tempSets = new HashSet<>();
        tempSets.addAll(dfaStateSet);
        int count = 0;
        do {
            count = dfaStateSet.size();
            for (var set : dfaStateSet) {
                for (Character symbol : dfa.getAlphabet()) {
                    HashSet<String> temp = new HashSet<>(); // the set we move to will remain empty if we don't add anything
                    HashSet<String> closure = new HashSet<>();
                    for (String state : set) {
                        if (this.delta.get(new Pair<>(state, symbol)) != null)
                            temp.addAll(this.delta.get(new Pair<>(state, symbol))); // we form a set of states that we transition to for each state
                    }
                    for (String s : temp) // for each state of the set in which we passed, we determine the closure
                        closure.addAll(this.closure(s));
                    tempSets.add(closure);
                }
            }
            dfaStateSet.addAll(tempSets);
        }while (count != dfaStateSet.size());
        this.changeAtributes(dfaStateSet,dfa);
        return dfa;
    }
    /*
     Implementation of the IRegularLanguage Interface.
     A method that converts EpsilonNfa to EpsilonNfa.
     */
    @Override
    public EpsilonNfa toENfa(){
        return this;
    }

    /*
     Implementation of the IRegularLanguage Interface.
     A method that returns the length of the shortest word.
     The most common word is detected by using BFS traversal, the idea is that stateLevel in HashMap remembers the level
     for each state, that level represents the number of transitions to that state. Let's use a row as an auxiliary structure
     for the implementation of the BFS algorithm. If the initial state is final, the shortest word is of length zero, in other cases
     we visit state by state and update the level for that state as the level of the previous one increased by one transition.
     The only difference from the algorithm we use with Dfa automata is that epsilon transitions are not counted as transitions.
     So if we reach a certain state through the epsilon transition, we do not count it as a new level, but as the level of the previous state.
     */
    @Override
    public int minimumLengthWord(){
        if(this.finalStates.contains(startState))
            return 0;
        HashMap<String,Integer> stateLevel = new HashMap<>();
        Queue<String> putVisited= new LinkedList<>();
        putVisited.add(this.startState);
        stateLevel.put(startState,0);
        while(!putVisited.isEmpty()){
            String state = putVisited.remove();
            for(var c : this.alphabet) {
                if (this.delta.get(new Pair<>(state, c)) != null) {
                    HashSet<String> transition = new HashSet<>();
                    transition.addAll(this.delta.get(new Pair<>(state, c)));
                    for (var el : transition) {
                        if (!stateLevel.containsKey(el)) {
                            if (c.equals(EpsilonNfa.EPSILON)) {
                                stateLevel.put(el, stateLevel.get(state));
                            } else {
                                stateLevel.put(el, stateLevel.get(state) + 1);
                            }
                            putVisited.add(el);
                            if(this.finalStates.contains(el)){
                                return stateLevel.get(el); // let's return the level of that element
                            }
                        }
                    }
                }
            }
        }
        return -1;
    }
    /*
     Implementation of the IRegularLanguage Interface.
     The method that determines the Union of two Regular Languages.
     The method performs the conversion to Dfa and uses the functionality of the Dfa automata union.
     */
    @Override
    public IRegularLanguage union(IRegularLanguage other){

        return (this.toDfa().union(other.toDfa())).toENfa();
    }
    /*
     Implementation of the IRegularLanguage Interface.
     A method that determines the Intersection of two Regular Languages.
     The method performs the conversion in Dfa and uses the functionality of the Dfa automatic intersection.
     */
    @Override
    public IRegularLanguage intersection(IRegularLanguage other) {

        return this.toDfa().intersection(other.toDfa()).toENfa();
    }
    /*
     Implementation of the IRegularLanguage Interface.
     A method that determines the Difference between two Regular Languages.
     The method converts to Dfa and uses the functionality of the Dfa automatic difference.
     */
    @Override
    public IRegularLanguage difference(IRegularLanguage other) {

        return this.toDfa().difference(other.toDfa()).toENfa();
    }
    /*
     Implementation of the IRegularLanguage Interface.
     A method that determines the equality of two Regular Languages.
     The method performs the conversion to Dfa and uses the functionality of the Dfa equality automaton.
    */
    @Override
    public boolean equality(IRegularLanguage other){

        return this.toDfa().equality(other.toDfa());
    }
    /*
     Implementation of the IRegularLanguage Interface.
     A method that determines the concatenation of two Regular Languages.
     The algorithm follows the definition, forming a new EpsilonNfa based on this and Regular Language
     which we pass as an argument, during which its conversion to EpsilonNfa is performed.
     A new automaton is obtained by adding one new epsilon transition that joins the final states
     this representation of the Regular Language with the final state of the second argument.
     */
    @Override
    public IRegularLanguage concatenation(IRegularLanguage other){
        EpsilonNfa epsilonNfa = new EpsilonNfa();
        EpsilonNfa otherENfa = (EpsilonNfa) other.toENfa();
        if(!this.alphabet.equals(otherENfa.getAlphabet())){
            throw new IllegalArgumentException();
        }
        if(other == null)
            throw new IllegalArgumentException();
        epsilonNfa.setAlphabet(this.getAlphabet());
        epsilonNfa.setStartState(this.startState);
        epsilonNfa.getStates().addAll(this.states);
        epsilonNfa.getStates().addAll(otherENfa.getStates()); // balances from both are added to the new machine
        epsilonNfa.getFinalStates().addAll(otherENfa.getFinalStates()); // add all the finals of the second
        epsilonNfa.getDelta().putAll(this.delta);
        epsilonNfa.getDelta().putAll(otherENfa.getDelta()); // let's insert all transitions
        for(var state : this.finalStates) { // connect all the finals of the first with epsilon to the initial of the second
            epsilonNfa.addTransition(state,EpsilonNfa.EPSILON,new String[]{otherENfa.getStartState()});
        }
        return epsilonNfa;
    }
    /*
     Implementation of the IRegularLanguage Interface.
     A method that determines the Cline star over Regular Language.
     The algorithm adds two new states, initial and final, and chains the new initial with the old by epsilon transitions
     beginner as well as with the new final state.
     While for each old final state EpsilonNfa, we chain with the new final and old initial state by epsilon transition.
     This allows us to repeat it once or more.
     */
    @Override
    public IRegularLanguage kleeneStar(){
        String startState = "Start_State";
        String finalState = "Final_State";
        EpsilonNfa epsilonNfa = new EpsilonNfa(startState);
        epsilonNfa.setAlphabet(this.alphabet);
        epsilonNfa.getStates().addAll(this.states);
        epsilonNfa.getStates().add(finalState);
        epsilonNfa.addFinalStates(finalState);
        epsilonNfa.getDelta().putAll(this.delta);
        epsilonNfa.addTransition(startState,EpsilonNfa.EPSILON,new String[]{this.startState,finalState});
        for(var state : this.finalStates){
            epsilonNfa.addTransition(state,EpsilonNfa.EPSILON,new String[]{finalState,this.startState});
        }

        return epsilonNfa;
    }
    /*
     Implementation of the IRegularLanguage Interface.
     A method that determines the complement of a Regular Language.
     The method converts to Dfa and uses the functionality of the Dfa automata complement.
    */
    @Override
    public IRegularLanguage complement(){

        return this.toDfa().complement().toENfa();
    }

    /*
      Implementation of the IRegularLanguage Interface.
      The method that determines the length of the longest word of the Regular Language.
      The method converts to Dfa and uses the functionality of the Dfa automaton maximumLengthWord.
     */
    @Override
    public int maximumLengthWord(){
        return this.toDfa().maximumLengthWord();
    }

    /*
     Implementation of the IRegularLanguage Interface.
     A method that determines the finiteness of a Regular Language.
     The method converts to Dfa and uses the functionality of the Dfa automata finality.
    */
    @Override
    public boolean finality(){
        return this.toDfa().finality();
    }

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
    public HashMap<Pair<String,Character>, HashSet<String>> getDelta()
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
    public void setAlphabet(HashSet<Character> alphabet)
    {
        this.alphabet = alphabet;
    }
    public void setDelta(HashMap<Pair<String,Character>, HashSet<String>> delta)
    {
        this.delta = delta;
    }
}
