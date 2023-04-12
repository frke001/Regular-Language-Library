package org.unibl.etf.regularExpression;

import org.unibl.etf.addition.*;

import org.unibl.etf.automata.*;
import org.unibl.etf.interfaces.IRegularLanguage;

import java.util.*;

public class RegularExpression implements IRegularLanguage {

    public static final Character CLEENE_STAR = '*';
    public static final Character CONCATENATION ='.';
    public static final Character ALTERNATIVE = '|';
    HashSet<Character> alphabet;
    private String regex;

    public RegularExpression(HashSet<Character> alphabet){ // We send the alphabet to the constructor
        this.alphabet = new HashSet<>();
        this.alphabet.addAll(alphabet);
    }
    public RegularExpression(){
        super();
    }
    /*
    A method for setting the Regex expression that we pass as an argument.
    The method checks whether the regular expression is concretely given, if symbols exist
    that do not belong to the alphabet an exception is thrown.
    In subtone, the setup is successful.
     */
    public void setRegex(String regex) {
        try {
            for (var el : regex.toCharArray()) {
                if (el != CONCATENATION && el != ALTERNATIVE && el != CLEENE_STAR && el != '(' && el != ')')
                    if (!alphabet.contains(el))
                        throw new IllegalArgumentException("String sadrzi simbole koji nisu iz alfabeta!");
            }
            String temp = regex.replaceAll("([a-z]|[*])([a-z])","$1.$2");
            this.regex = temp;
        }catch (IllegalArgumentException e){
            this.regex = null;
            System.out.println(e.getMessage());
        }
    }

    /*
    A method for checking whether a string belongs to a language.
    This method uses the function public boolean matches( @NonNls @NotNull String regex )
    which returns true if the input string matches the given regex
     */
    public boolean accept(String input){
        if(this.regex != null) {
            String temp = regex.replaceAll("[.]", "");
            return input.matches(temp);
        }
        return false;
    }

    /*
    Helper method that creates and returns the Priority Table.
    The table contains a pair of priorities that define the init and stack priorities.
    Operators that have the same priority also have the same init and stack priority.
     */
    private HashMap<Character, PriorityPair> getTable(){
        HashMap<Character,PriorityPair> tableOFPriorities = new HashMap<>();
        tableOFPriorities.put(this.CLEENE_STAR,new PriorityPair(6,6));
        tableOFPriorities.put(this.CONCATENATION,new PriorityPair(5,5));
        tableOFPriorities.put(this.ALTERNATIVE,new PriorityPair(4,4));
        tableOFPriorities.put('(',new PriorityPair(7,0));
        tableOFPriorities.put(')',new PriorityPair(1,0));
        return tableOFPriorities;
    }

    /*
    A helper method that converts a regular expression from infix form to postfix form.
    The algorithm scans the input infix expression, as soon as it finds an operand, it is added to the postfix form.
    If an operator is encountered, they are placed on the stack and that:
    If the operator is an or parentheses and its input priority is <= than the stack priority of the element at the top of the stack
    we remove the element from the stack and transfer it to postfix until we reach the element whose stack is the priority
    less than the init priority of the current operator.
    The left parenthesis is immediately placed on the stack, and when the right parenthesis appears, everything up to the first left parenthesis is removed from the stack.
     */
    private String regularExpressionToPostfix(){
        Stack<Character> stack = new Stack<>();
        StringBuilder postfixBuilder = new StringBuilder();
        HashMap<Character,PriorityPair> tableOfPriorities = new HashMap<>();
        tableOfPriorities = this.getTable();

        for(var character : this.regex.toCharArray()){

            if(Character.isAlphabetic(character)){
                postfixBuilder.append(character);
            }else{
                while(!stack.isEmpty() && tableOfPriorities.get(character).initPriority <= tableOfPriorities.get(stack.peek()).stackPriority){
                    Character operator = stack.pop();
                    postfixBuilder.append(operator);
                }
                if(character != ')')
                    stack.push(character);
                else
                    stack.pop();
            }
        }
        while(!stack.isEmpty()){
            Character operator = stack.pop();
            postfixBuilder.append(operator);
        }
        return postfixBuilder.toString();
    }
    /*
     Implementation of the IRegularLanguage Interface.
     A method that performs a RegularExpression to EpsilonNfa conversion.
     The algorithm goes through the postfix expression and if it encounters an operand - a symbol
     an automaton with two states and a transition between them is formed for that symbol and placed on the stack.
     We add only a couple of initial final states to the stack.
     If the element is *, we remove the element and its initial and final state from the stack
     chain in the way the CleeneStar algorithm is defined, add a new initial and final
     and we connect the new initial epsilon transition to the initial element from the stack, and to the new final one,
     while the old final epsilon transition is connected to the new final and the old initial.
     After that, we put the newly created automaton (a pair of initial and final states) on the stack.
     This procedure also applies to operations | and . except that we remove two elements from the stack and apply logic
     concatenation and union.
    */
    private String prefix(){
        Random rand = new Random();
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < 4; i++){
            res.append(alphabet.charAt(rand.nextInt(26)));
        }
        return res.toString();
    }
    @Override
    public EpsilonNfa toENfa(){
        String namePrefix = this.prefix();
        EpsilonNfa epsilonNfa = new EpsilonNfa();
        String postfixRepresentation = this.regularExpressionToPostfix();
        Stack<Pair<String,String>> stack = new Stack<>();
        int nameEditor = 0;
        for(var character : postfixRepresentation.toCharArray()){
            if(Character.isAlphabetic(character)){
                String st1 = namePrefix + nameEditor++;
                String st2 = namePrefix + nameEditor++;
                epsilonNfa.addTransition(st1, character ,new String[]{st2});
                stack.push(new Pair<>(st1,st2));
            }
            else{ // if it is any of the operators
                if(character == RegularExpression.ALTERNATIVE){ // we remove two operands from the stack
                    Pair<String,String> op2 = stack.pop();
                    Pair<String,String> op1 = stack.pop();
                    String additionallyFirst = namePrefix + nameEditor++;
                    String additionallyFinal = namePrefix + nameEditor++;

                    epsilonNfa.addTransition(additionallyFirst,EpsilonNfa.EPSILON,new String[]{ op1.first,op2.first}); // new beginning with the beginning of the first operand
                    // new beginning with the beginning of the second operand
                    epsilonNfa.addTransition(op1.second,EpsilonNfa.EPSILON,new String[]{ additionallyFinal});
                    epsilonNfa.addTransition(op2.second,EpsilonNfa.EPSILON,new String[] {additionallyFinal}); // the final of both in the new final

                    stack.push(new Pair<>(additionallyFirst,additionallyFinal));
                }else if(character == RegularExpression.CONCATENATION){
                    Pair<String,String> op2 = stack.pop();
                    Pair<String,String> op1 = stack.pop();
                    epsilonNfa.addTransition(op1.second,EpsilonNfa.EPSILON,new String[]{op2.first});

                    stack.push(new Pair<>(op1.first,op2.second));
                }else if(character == RegularExpression.CLEENE_STAR){
                    Pair<String,String> op = stack.pop();
                    String additionallyFirst = namePrefix + nameEditor++;
                    String additionallyFinal = namePrefix + nameEditor++;
                    epsilonNfa.addTransition(additionallyFirst,EpsilonNfa.EPSILON,new String[]{op.first,additionallyFinal});
                    epsilonNfa.addTransition(op.second,EpsilonNfa.EPSILON,new String[]{op.first,additionallyFinal});

                    stack.push(new Pair<>(additionallyFirst,additionallyFinal));
                }
            }
        }
        Pair<String,String> mainStatesOfNfa = stack.pop();
        epsilonNfa.setStartState(mainStatesOfNfa.first);
        epsilonNfa.addFinalStates(mainStatesOfNfa.second);
        epsilonNfa.getAlphabet().addAll(this.alphabet);
        return epsilonNfa;
    }
    /*
    Implementation of the IRegularLanguage Interface.
    The method that performs the conversion of RegularExpression to EpsilonNfa and then to Dfa.
     */
    @Override
    public Dfa toDfa(){

        return this.toENfa().toDfa();
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
     The method that determines the Union of two Regular Languages.
     The method forms a Regular Expression that is obtained by concatenating the regex operands with the operator |.
     If it receives a Regular Expression representation that is not a RegularExpression as a parameter, executes
     is the conversion of this object to Dfa and the use of the functionality of the Dfa automata union.
     */
    @Override
    public IRegularLanguage union(IRegularLanguage other){
        /*return this.evaluate(other, new FunctionParameter() {
            @Override
            public String form(String s1, String s2) {
                return s1 + "|" + s2;
            }
        });*/
        Dfa otherDfa = (Dfa) other.toDfa();
        Dfa thisDfa = (Dfa) this.toDfa();

        if(!thisDfa.getAlphabet().equals(otherDfa.getAlphabet()))
            throw new IllegalArgumentException();
        if(other == null){
            throw new IllegalArgumentException();
        }
        if(!(other instanceof RegularExpression)){
            return this.toDfa().union(other.toDfa());
        }
        RegularExpression result = new RegularExpression(this.alphabet);
        result.setRegex(this.regex+ "|" + ((RegularExpression) other).regex);
        return result;
    }
    /*
     Implementation of the IRegularLanguage Interface.
     A method that determines the intersection of two Regular Languages.
     The method performs the conversion in Dfa and uses the functionality of the Dfa automatic intersection.
    */
    @Override
    public IRegularLanguage intersection(IRegularLanguage other){

        return this.toDfa().intersection(other.toDfa());
    }
    /*
     Implementation of the IRegularLanguage Interface.
     A method that determines the difference between two Regular Languages.
     The method converts to Dfa and uses the functionality of the Dfa automatic difference.
    */
    @Override
    public IRegularLanguage difference(IRegularLanguage other){

        return this.toDfa().difference(other.toDfa());
    }
    /*
     Implementation of the IRegularLanguage Interface.
     A method that determines the concatenation of two Regular Languages.
     The method forms a Regular Expression that is obtained by concatenating the regex operands with the operator "."
     If it receives a Regular Expression representation that is not a RegularExpression as a parameter, executes
     is the conversion of this object into EpsilonNfa and the use of the functionality of the EpsilonNfa automata concatenation.
     */
    @Override
    public IRegularLanguage concatenation(IRegularLanguage other){
        Dfa otherDfa = (Dfa) other.toDfa();
        Dfa thisDfa = (Dfa) this.toDfa();

        if(!thisDfa.getAlphabet().equals(otherDfa.getAlphabet()))
            throw new IllegalArgumentException();
        if(other == null){
            throw new IllegalArgumentException();
        }
        if(!(other instanceof RegularExpression)){
            return this.toENfa().concatenation(other.toENfa());
        }
        RegularExpression result = new RegularExpression(this.alphabet);
        result.setRegex(this.regex+ "." + ((RegularExpression) other).regex);
        return result;
    }
    /*
     Implementation of the IRegularLanguage Interface.
     The method that determines the Klinian star of Regular Language.
     The method forms a Regular Expression by adding ()* to an existing regex;
     */
    @Override
    public IRegularLanguage kleeneStar(){
        RegularExpression result = new RegularExpression(this.alphabet);
        result.setRegex("("+this.regex+")*");
        return result;
    }
    /*
     Implementation of the IRegularLanguage Interface.
     A method that determines the complement of a Regular Language.
     The method converts to Dfa and uses the functionality of the Dfa automata complement.
    */
    public IRegularLanguage complement(){

        return this.toDfa().complement();
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
     Implementation of the IRregular Language Interface.
     A method that determines the length of the shortest Regular Language word.
     The method performs the conversion in Dfa and uses the functionality of the Dfa machine minimumLengthWord.
   */
    @Override
    public int minimumLengthWord(){
        return this.toDfa().minimumLengthWord();
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

    public HashSet<Character> getAlphabet(){
        return this.alphabet;
    }
    public String getRegex(){
        return this.regex;
    }
}
