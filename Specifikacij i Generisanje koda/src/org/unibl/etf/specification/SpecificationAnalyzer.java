package org.unibl.etf.specification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.unibl.etf.automata.*;
import org.unibl.etf.interfaces.IRegularLanguage;
import org.unibl.etf.regularExpression.RegularExpression;

public class SpecificationAnalyzer {

    private String source;
    private int errors = 0;
    private ArrayList<Integer> lineNumber = new ArrayList<>(10);

    public SpecificationAnalyzer(String source){
        this.source = source;
    }

    public ArrayList<Token> lexicalAnalysis(){

        for(int i = 0; i < 10; i++ )
            lineNumber.add(0);
        errors = 0;
        ArrayList<Token> tokenList = new ArrayList<>();

        String lines[] = source.split("\n");
        if(lines.length == 0){
            errors++;
            //throw new UnsupportedTokenException("Lexical error!");
        }

        if(lines[0].substring(0,lines[0].length() - 1).equals("DFA")) {
            tokenList.add(new Token("Representation",lines[0].substring(0,lines[0].length() - 1)));
            lexicalAnalysisDFA(tokenList,lines);
        }
        else if(lines[0].substring(0,lines[0].length() - 1).equals("EPSILON NFA")) {
            tokenList.add(new Token("Representation",lines[0].substring(0,lines[0].length() - 1)));
            lexicalAnalysisENFA(tokenList,lines);
        }
        else if(lines[0].substring(0,lines[0].length() - 1).equals("REGULAR EXPRESSION")) {
            tokenList.add(new Token("Representation",lines[0].substring(0,lines[0].length() - 1)));
            lexicalAnalysisRE(tokenList,lines);
        }
        else {
            errors++;
            lineNumber.set(0,1);
            //throw new UnsupportedTokenException("Lexical error!");
        }
        if(tokenList.size() > 1)
            return tokenList;
        else {
            System.out.println("Postoje " + errors + " leksicke greske");
            System.out.println("Greske u linijama: ");
            for(var el : lineNumber)
                if(el != 0){
                    System.out.print((lineNumber.indexOf(el) + 1) + " ");
                }
            System.out.println();
            return null;
        }
    }
    private boolean check(ArrayList<Token> tokenList,String lines[]) {
        Boolean status = true;
        int i = 0,j=0;
        Random rand = new Random();
        String linesNew[] = new String[lines.length];
        for(String line : lines) {
            if(!line.endsWith(";")){
                errors++;
                lineNumber.set(i,rand.nextInt(1000));
                status = false;
            }
            i++;
            linesNew[j++] = line.substring(0,line.length()-1);
        }
        lines = linesNew;

        if (lines.length != 7) {
            errors++;
            status = false;
            return status;
        }
        String states[] = lines[1].split(",");
        for (String state : states) {
            if (state.length() == 0 || state.contains(" ")) {
                status = false;
                lineNumber.set(1, rand.nextInt(1000));
                errors++;
            }
        }
        String startState[] = lines[2].split(",");
        if (startState.length != 1 || startState.toString().contains(" ")) {
            errors++;
            lineNumber.set(2, rand.nextInt(1000));
            status = false;
        }
        String finalStates[] = lines[3].split(",");
        for (String state : finalStates) {
            if (state.length() == 0 || state.contains(" ")) {
                status = false;
                lineNumber.set(3, rand.nextInt(1000));
                errors++;
            }
        }
        String transitions[] = lines[5].split(",");
        for(String transition : transitions){
            if(transition.length() == 0) {
                errors++;
                lineNumber.set(5, rand.nextInt(1000));
                status = false;
            }else {
                String elements[] = transition.split("-");
                if (elements.length != 3) {
                    errors++;
                    lineNumber.set(5, rand.nextInt(1000));
                    status = false;
                }else{
                    for(String el : elements)
                        if(el.contains(" ")){
                            errors++;
                            lineNumber.set(5, rand.nextInt(1000));
                            status = false;
                        }
                }
            }
        }
        String words[] = lines[6].split(",");
        for(String word : words){
            if(word.length() == 0) {
                errors++;
                lineNumber.set(6,rand.nextInt(1000));
                status = false;
            }
        }
        return status;
    }
    public void lexicalAnalysisDFA(ArrayList<Token> tokenList,String lines[]){

        Random rand = new Random();
        Boolean status = true;
        status = this.check(tokenList,lines);
        String linesNew[] = new String[lines.length];
        int j = 0;
        for(String line : lines) {
            linesNew[j++] = line.substring(0,line.length()-1);
        }
        lines = linesNew;

        String alphabet[] = lines[4].split(",");
        for(String symbol : alphabet){
            if(symbol.length() != 1 || isAlphaNumerical(symbol) || symbol.contains(" ")){
                errors++;
                lineNumber.set(4,rand.nextInt(1000));
                status = false;
            }
        }
        if(status) {
            tokenList.add(new Token("States", lines[1]));
            tokenList.add(new Token("Start State", lines[2]));
            tokenList.add(new Token("FinalStates", lines[3]));
            tokenList.add(new Token("Alphabet", lines[4]));
            tokenList.add(new Token("Transitions", lines[5]));
            tokenList.add(new Token("Words", lines[6]));
        }
    }

    public void lexicalAnalysisENFA(ArrayList<Token> tokenList,String lines[]){


        Random rand = new Random();
        Boolean status = true;
        status = this.check(tokenList,lines);

        String linesNew[] = new String[lines.length];
        int j = 0;
        for(String line : lines) {
            linesNew[j++] = line.substring(0,line.length()-1);
        }
        lines = linesNew;

        String alphabet[] = lines[4].split(",");
        for(String symbol : alphabet){
            if(symbol.length() != 1 || isAlphaNumericalEpsilon(symbol) || symbol.contains(" ")){
                errors++;
                lineNumber.set(4,rand.nextInt(1000));
                status = false;
            }
        }
        if(status) {
            tokenList.add(new Token("States", lines[1]));
            tokenList.add(new Token("Start State", lines[2]));
            tokenList.add(new Token("FinalStates", lines[3]));
            tokenList.add(new Token("Alphabet", lines[4]));
            tokenList.add(new Token("Transitions", lines[5]));
            tokenList.add(new Token("Words", lines[6]));
        }
    }
    public void lexicalAnalysisRE(ArrayList<Token> tokenList,String lines[]){

        Boolean status = true;
        int i = 0,j=0;
        Random rand = new Random();
        String linesNew[] = new String[lines.length];
        for(String line : lines) {
            if(!line.endsWith(";")){
                errors++;
                lineNumber.set(i,rand.nextInt(1000));
                status = false;
            }
            i++;
            linesNew[j++] = line.substring(0,line.length()-1);
        }
        lines = linesNew;

        if (lines.length != 4) {
            errors++;
            status = false;
            return;
        }
        String alphabet[] = lines[1].split(",");
        for (String symbol : alphabet) {
            if (symbol.length() != 1 || isAlphaNumericalEpsilon(symbol) || symbol.contains(" ")) {
                errors++;
                lineNumber.set(1, rand.nextInt(1000));
                status = false;
            }
        }
        String regex = lines[2];
        if (regex.contains(" ") || regex.contains("..") || regex.contains("**") || regex.contains("||")) {
            errors++;
            lineNumber.set(2, rand.nextInt(1000));
            status = false;
        }
        try {
            Pattern p = Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            errors++;
            lineNumber.set(2, rand.nextInt(1000));
            status = false;
        }
        String words[] = lines[3].split(",");
        for (String word : words) {
            if (word.length() == 0) {
                errors++;
                lineNumber.set(3, rand.nextInt(1000));
                status = false;
            }
        }
        if(status) {
            tokenList.add(new Token("Alphabet", lines[1]));
            tokenList.add(new Token("Regex", lines[2]));
            tokenList.add(new Token("Words", lines[3]));
        }

    }

    public IRegularLanguage acceptance(){
        ArrayList<Token> tokenList = this.lexicalAnalysis();
        if(tokenList == null) {
            System.out.println("Nije moguce ispitati pripadnost stringova reprezentovanom jeziku");
            return null;
        }
        else if(tokenList.get(0).getSemanticValue().equals("DFA"))
            return acceptanceByDFA(tokenList);
        else if(tokenList.get(0).getSemanticValue().equals("EPSILON NFA"))
            return acceptanceByENFA(tokenList);
        else
            return acceptanceByRE(tokenList);
    }

    private IRegularLanguage acceptanceByDFA(ArrayList<Token> tokenList){

        String alphabet[] = tokenList.get(4).getSemanticValue().split(",");
        Character alp[] = new Character[alphabet.length];
        int i = 0;
        for(var symbol : alphabet)
            alp[i++] = symbol.charAt(0);
        Dfa dfa = new Dfa(tokenList.get(2).getSemanticValue(),alp);
        String transitions[] = tokenList.get(5).getSemanticValue().split(",");
        for(String transition : transitions){
            String elements[] = transition.split("-");
            dfa.addTransitionVol_2(elements[0],elements[1].charAt(0),elements[2]);
        }
        String finalStates[] = tokenList.get(3).getSemanticValue().split(",");
        for(String state : finalStates)
            dfa.addFinalStates(state);
        String words[] = tokenList.get(6).getSemanticValue().split(",");
        for(String word : words){
            System.out.println("Automat " + (dfa.accept(word)? "prihvata" : "ne prihvata") + " rijec " + word);
        }
        return dfa;
    }

    private IRegularLanguage acceptanceByENFA(ArrayList<Token> tokenList){

        String alphabet[] = tokenList.get(4).getSemanticValue().split(",");
        Character alp[] = new Character[alphabet.length];
        int i = 0;
        for(var symbol : alphabet)
            alp[i++] = symbol.charAt(0);
        EpsilonNfa epsilonNfa = new EpsilonNfa(tokenList.get(2).getSemanticValue(),alp);
        String transitions[] = tokenList.get(5).getSemanticValue().split(",");
        for(String transition : transitions){
            String elements[] = transition.split("-");
            String nextStates[] = elements[2].split("/");
            epsilonNfa.addTransitionVol_2(elements[0],elements[1].charAt(0),nextStates);
        }
        String finalStates[] = tokenList.get(3).getSemanticValue().split(",");
        for(String state : finalStates)
            epsilonNfa.addFinalStates(state);
        String words[] = tokenList.get(6).getSemanticValue().split(",");
        for(String word : words){
            System.out.println("Automat " + (epsilonNfa.accept(word)? "prihvata" : "ne prihvata") + " rijec " + word);
        }
        return epsilonNfa;
    }

    private IRegularLanguage acceptanceByRE(ArrayList<Token> tokenList){

        HashSet<Character>alphabet = new HashSet<>();
        String alp[] = tokenList.get(1).getSemanticValue().split(",");
        for(String symbol : alp)
            alphabet.add(symbol.charAt(0));

        RegularExpression regex = new RegularExpression(alphabet);
        regex.setRegex(tokenList.get(2).getSemanticValue());
        String words[] = tokenList.get(3).getSemanticValue().split(",");
        for(String word : words){
            System.out.println("Automat " + (regex.accept(word)? "prihvata" : "ne prihvata") + " rijec " + word);
        }
        return regex;

    }

    private boolean isAlphaNumerical(String symbol){
        return Character.isAlphabetic(symbol.charAt(0)) && Character.isDigit(symbol.charAt(0));
    }
    private boolean isAlphaNumericalEpsilon(String symbol){
        String epsilon = "";
        epsilon += EpsilonNfa.EPSILON;
        return Character.isAlphabetic(symbol.charAt(0)) && Character.isDigit(symbol.charAt(0))
                && symbol.equals(epsilon);
    }
}
