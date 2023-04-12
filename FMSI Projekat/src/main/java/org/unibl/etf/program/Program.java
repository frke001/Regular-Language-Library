package org.unibl.etf.program;


import org.unibl.etf.automata.*;
import org.unibl.etf.regularExpression.*;
import org.unibl.etf.addition.*;


public class Program {
    public static void main(String args[]){


        Dfa test3 = new Dfa("q0");
        test3.addTransition("q0",'a',"q0");
        test3.addTransition("q0",'b',"q1");
        test3.addTransition("q1",'a',"q2");
        test3.addTransition("q1",'b',"q2");
        test3.addTransition("q2",'a',"q2");
        test3.addTransition("q2",'b',"q2");
        test3.addFinalStates("q1");

        Dfa test4 = new Dfa("q3");
        test4.addTransition("q3",'a',"q4");
        test4.addTransition("q3",'b',"q3");
        test4.addTransition("q4",'a',"q3");
        test4.addTransition("q4",'b',"q4");
        test4.addFinalStates("q4");

        Dfa test = new Dfa();
        test = (Dfa)test3.union(test4);


        Dfa finalityDfa = new Dfa("q0");
        finalityDfa.addTransition("q0",'a',"q1");
        finalityDfa.addTransition("q0",'b',"q2");
        finalityDfa.addTransition("q1",'a',"q0");
        finalityDfa.addTransition("q1",'b',"q1");
        finalityDfa.addTransition("q2",'a',"q3");
        finalityDfa.addTransition("q2",'b',"q4");
        finalityDfa.addTransition("q3",'a',"q5");
        finalityDfa.addTransition("q3",'b',"q5");
        finalityDfa.addTransition("q4",'a',"q2");
        finalityDfa.addTransition("q4",'b',"q4");
        finalityDfa.addTransition("q5",'a',"q3");
        finalityDfa.addTransition("q5",'b',"q3");

        //finalityDfa.addFinalStates("q1");
        finalityDfa.addFinalStates("q4");


        System.out.println("Konasnost : " + finalityDfa.finality());
        System.out.println("Najduza : " + finalityDfa.maximumLengthWord());
        System.out.println("Najkraca : " + finalityDfa.minimumLengthWord());

        Dfa finalityDfa1 = new Dfa("q0");
        finalityDfa1.addTransition("q0",'a',"q3");
        finalityDfa1.addTransition("q0",'b',"q1");
        finalityDfa1.addTransition("q1",'a',"q2");
        finalityDfa1.addTransition("q1",'b',"q2");
        finalityDfa1.addTransition("q2",'a',"q2");
        finalityDfa1.addTransition("q2",'b',"q2");
        finalityDfa1.addTransition("q3",'a',"q3");
        finalityDfa1.addTransition("q3",'b',"q3");

        finalityDfa1.addFinalStates("q1");

        System.out.println("Konasnost : " + finalityDfa1.finality());
        System.out.println("Najduza : " + finalityDfa1.maximumLengthWord());
        System.out.println("Najkraca : " + finalityDfa1.minimumLengthWord());
        System.out.println("Automat " + (finalityDfa.accept("bb")? "prihvata" : "ne prihvata") + " rijec " + "b");
        System.out.println(finalityDfa.accept("bb"));
    }
}

