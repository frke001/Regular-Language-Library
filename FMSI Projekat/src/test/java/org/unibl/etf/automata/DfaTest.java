package org.unibl.etf.automata;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.unibl.etf.exceptions.DuplicateTransitionException;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class DfaTest {

    @Test
    @DisplayName("Checking if DFA accepts input string.")
    void shouldAcceptInputString() {
        Dfa automat1 = new Dfa("q0");

        automat1.addTransition("q0", '0', "q1");
        automat1.addTransition("q0", '1', "q1");
        automat1.addTransition("q1", '0', "q2");
        automat1.addTransition("q1", '1', "q2");
        automat1.addTransition("q2", '0', "q3");
        automat1.addTransition("q2", '1', "q3");
        automat1.addTransition("q3", '0', "q3");
        automat1.addTransition("q3", '1', "q3");

        automat1.addFinalStates("q2");

        Assertions.assertEquals(true,automat1.accept("00"));
        Assertions.assertEquals(true,automat1.accept("01"));
        Assertions.assertEquals(true,automat1.accept("10"));
        Assertions.assertEquals(true,automat1.accept("11"));
    }

    @Test
    @DisplayName("Checking if DFA is not accepting input string. ")
    void shouldNotAcceptInputString(){
        Dfa A = new Dfa("q0");
        A.addTransition("q0", 'b', "q1");
        A.addTransition("q0", 'a', "q5");
        A.addTransition("q1", 'a', "q2");
        A.addTransition("q1", 'b', "q0");
        A.addTransition("q2", 'a', "q4");
        A.addTransition("q2", 'b', "q7");
        A.addTransition("q3", 'a', "q6");
        A.addTransition("q3", 'b', "q1");
        A.addTransition("q4", 'a', "q2");
        A.addTransition("q4", 'b', "q7");
        A.addTransition("q5", 'a', "q0");
        A.addTransition("q5", 'b', "q5");
        A.addTransition("q6", 'a', "q7");
        A.addTransition("q6", 'b', "q5");
        A.addTransition("q7", 'a', "q7");
        A.addTransition("q7", 'b', "q7");

        A.addFinalStates("q1");
        A.addFinalStates("q2");
        A.addFinalStates("q6");

        Assertions.assertEquals(false,A.accept("abba"));
        Assertions.assertEquals(false,A.accept("bbbbbbabbbb"));

    }
    @Test
    @DisplayName("Adding existing transition.")
    void shouldThrowDuplicateTransitionExceptionWhenAddingExistingTransition(){
        Dfa DuplicateTransition = new Dfa("q0");

        DuplicateTransition.addTransition("q0",'a',"q1");
        DuplicateTransition.addTransition("q0",'b',"q0");
        DuplicateTransition.addTransition("q1",'a',"q0");
        DuplicateTransition.addTransition("q1",'b',"q1");


        Assertions.assertThrows(DuplicateTransitionException.class, ()-> {
            DuplicateTransition.addTransition("q1",'a',"q0");
        });
    }

    @Test
    @DisplayName("Minimizing DFA.")
    void shouldAcceptEqualLanguageAsMinimizedAutomata() {
        Dfa A = new Dfa("q0");
        A.addTransition("q0", 'b', "q1");
        A.addTransition("q0", 'a', "q5");
        A.addTransition("q1", 'a', "q2");
        A.addTransition("q1", 'b', "q0");
        A.addTransition("q2", 'a', "q4");
        A.addTransition("q2", 'b', "q7");
        A.addTransition("q3", 'a', "q6");
        A.addTransition("q3", 'b', "q1");
        A.addTransition("q4", 'a', "q2");
        A.addTransition("q4", 'b', "q7");
        A.addTransition("q5", 'a', "q0");
        A.addTransition("q5", 'b', "q5");
        A.addTransition("q6", 'a', "q7");
        A.addTransition("q6", 'b', "q5");
        A.addTransition("q7", 'a', "q7");
        A.addTransition("q7", 'b', "q7");

        A.addFinalStates("q1");
        A.addFinalStates("q2");
        A.addFinalStates("q6");

        Assertions.assertEquals(true,A.accept("bbaab"));

        A.minimize();

        Assertions.assertEquals(true,A.accept("bbaab"));
    }

    @Test
    @DisplayName("Finding length of shortest word to be accepted.")
    void minimumLengthWord() {
        Dfa A2 = new Dfa("q0");

        A2.addTransition("q0", 'b', "q1");
        A2.addTransition("q0", 'a', "q4");
        A2.addTransition("q1", 'a', "q3");
        A2.addTransition("q1", 'b', "q2");
        A2.addTransition("q2", 'a', "q3");
        A2.addTransition("q2", 'b', "q2");
        A2.addTransition("q3", 'a', "q2");
        A2.addTransition("q3", 'b', "q3");
        A2.addTransition("q4", 'a', "q0");
        A2.addTransition("q4", 'b', "q5");
        A2.addTransition("q5", 'a', "q2");
        A2.addTransition("q5", 'b', "q3");

        A2.addFinalStates("q3");

        Assertions.assertEquals(2,A2.minimumLengthWord());

        A2.addFinalStates("q1");

        Assertions.assertEquals(1,A2.minimumLengthWord());
    }

    @Test
    @DisplayName("Finding maximum length word in Finite Automata.")
    void SholudFindMaximumLengthWord() {

        Dfa automat1 = new Dfa("q0");

        automat1.addTransition("q0", '0', "q1");
        automat1.addTransition("q0", '1', "q1");
        automat1.addTransition("q1", '0', "q2");
        automat1.addTransition("q1", '1', "q2");
        automat1.addTransition("q2", '0', "q3");
        automat1.addTransition("q2", '1', "q3");
        automat1.addTransition("q3", '0', "q3");
        automat1.addTransition("q3", '1', "q3");

        automat1.addFinalStates("q2");

        Assertions.assertEquals(2,automat1.maximumLengthWord());
    }
    @Test
    @DisplayName("DFA accept infinte language,should not find maximum lwngth word.")
    /**
     * This automata acceps language that contains words with not even number of symbol 1
     */
    void SholudNotFindMaximumLengthWord() {

        Dfa automat1 = new Dfa("q0");

        automat1.addTransition("q0", '0', "q0");
        automat1.addTransition("q0", '1', "q1");
        automat1.addTransition("q1", '0', "q1");
        automat1.addTransition("q1", '1', "q0");
        automat1.addFinalStates("q1");

        Assertions.assertEquals(-1,automat1.maximumLengthWord());
    }
    @Test
    @DisplayName("Testing equality between Regular Language representations.")
    void equality() {
        EpsilonNfa E4 = new EpsilonNfa("q0");
        E4.addTransition("q0", 'a', new String[]{"q1"});
        E4.addTransition("q0", 'b', new String[]{"q0"});
        E4.addTransition("q1", 'b', new String[]{"q1"});
        E4.addTransition("q1", EpsilonNfa.EPSILON, new String[]{"q2","q4"});
        E4.addTransition("q2", 'a', new String[]{"q2"});
        E4.addTransition("q2", 'b', new String[]{"q5"});
        E4.addTransition("q3", 'a', new String[]{"q4"});
        E4.addTransition("q4", 'a', new String[]{"q5"});
        E4.addTransition("q4", 'b', new String[]{"q3"});

        E4.addFinalStates("q5");

        Dfa dfa = E4.toDfa();

        Assertions.assertEquals(true,E4.equality(dfa));

        dfa.minimize();

        Assertions.assertEquals(true,E4.equality(dfa));

    }

    @Test
    @DisplayName("Checking if union of two DFA accepts both languages")
    /**
     * First automata accepts words in form (a^n)b
     * Second autoata accepts words that contains not even number of symbol a
     */
    void shouldAcceptWordsFromBothLanguagesRepresentedByThisDFA() {

        Dfa A1 = new Dfa("q0");
        A1.addTransition("q0",'a',"q0");
        A1.addTransition("q0",'b',"q1");
        A1.addTransition("q1",'a',"q2");
        A1.addTransition("q1",'b',"q2");
        A1.addTransition("q2",'a',"q2");
        A1.addTransition("q2",'b',"q2");
        A1.addFinalStates("q1");

        Assertions.assertEquals(true,A1.accept("aaaab"));

        Dfa A2 = new Dfa("q3");
        A2.addTransition("q3",'a',"q4");
        A2.addTransition("q3",'b',"q3");
        A2.addTransition("q4",'a',"q3");
        A2.addTransition("q4",'b',"q4");
        A2.addFinalStates("q4");

        Assertions.assertEquals(true,A2.accept("abaab"));

        Dfa unionDfa = (Dfa)A1.union(A2);

        Assertions.assertEquals(true,unionDfa.accept("aaab"));
        Assertions.assertEquals(true,unionDfa.accept("bbba"));

    }

    @Test
    void shouldAcceptOnlyWordsThatAreAcceptedFromBothAutomata() {
        Dfa A1 = new Dfa("q0");
        A1.addTransition("q0",'a',"q0");
        A1.addTransition("q0",'b',"q1");
        A1.addTransition("q1",'a',"q2");
        A1.addTransition("q1",'b',"q2");
        A1.addTransition("q2",'a',"q2");
        A1.addTransition("q2",'b',"q2");
        A1.addFinalStates("q1");

        Assertions.assertEquals(true,A1.accept("aaaab"));

        Dfa A2 = new Dfa("q3");
        A2.addTransition("q3",'a',"q4");
        A2.addTransition("q3",'b',"q3");
        A2.addTransition("q4",'a',"q3");
        A2.addTransition("q4",'b',"q4");
        A2.addFinalStates("q4");

        Assertions.assertEquals(true,A2.accept("abaab"));

        Dfa intersectionDfa = (Dfa)A1.intersection(A2);

        Assertions.assertEquals(true,intersectionDfa.accept("aaab"));
        Assertions.assertEquals(false,intersectionDfa.accept("bbba"));
    }

    @Test
    void shouldAcceptOnlyWordsThatAreAcceptedByFirstAutomataNotBySecond() {
        Dfa A1 = new Dfa("q0");
        A1.addTransition("q0",'a',"q0");
        A1.addTransition("q0",'b',"q1");
        A1.addTransition("q1",'a',"q2");
        A1.addTransition("q1",'b',"q2");
        A1.addTransition("q2",'a',"q2");
        A1.addTransition("q2",'b',"q2");
        A1.addFinalStates("q1");

        Assertions.assertEquals(true,A1.accept("aaaab"));

        Dfa A2 = new Dfa("q3");
        A2.addTransition("q3",'a',"q4");
        A2.addTransition("q3",'b',"q3");
        A2.addTransition("q4",'a',"q3");
        A2.addTransition("q4",'b',"q4");
        A2.addFinalStates("q4");

        Assertions.assertEquals(true,A2.accept("abaab"));

        Dfa differenceDfa = (Dfa)A1.difference(A2);

        Assertions.assertEquals(true,differenceDfa.accept("aaaab"));
        Assertions.assertEquals(false,differenceDfa.accept("aaab"));
    }

    @Test
    /**
     * First automata accepts word ab
     * Second automata accepts word aa
     */

    void concatenation() {
        Dfa first = new Dfa("q0");

        first.addTransition("q0",'a',"q1");
        first.addTransition("q0",'b',"q3");
        first.addTransition("q1",'a',"q3");
        first.addTransition("q1",'b',"q2");
        first.addTransition("q2",'a',"q3");
        first.addTransition("q2",'b',"q3");
        first.addTransition("q3",'a',"q3");
        first.addTransition("q3",'b',"q3");
        first.addFinalStates("q2");

        Assertions.assertEquals(true,first.accept("ab"));
        Assertions.assertEquals(false,first.accept("aab"));

        Dfa second = new Dfa("q4");

        second.addTransition("q4",'a',"q5");
        second.addTransition("q4",'b',"q7");
        second.addTransition("q5",'a',"q6");
        second.addTransition("q5",'b',"q7");
        second.addTransition("q6",'a',"q7");
        second.addTransition("q6",'b',"q7");
        second.addTransition("q7",'a',"q7");
        second.addTransition("q7",'b',"q7");
        second.addFinalStates("q6");

        Assertions.assertEquals(true,second.accept("aa"));
        Assertions.assertEquals(false,second.accept("aab"));

        Dfa concatenationDfa = (Dfa)first.concatenation(second);

        Assertions.assertEquals(true,concatenationDfa.accept("abaa"));
        Assertions.assertEquals(false,concatenationDfa.accept("aaba"));
    }

    @Test
    /**
     * Automata accepts word a
     */
    void kleeneStar() {

        Dfa A = new Dfa("q0");

        A.addTransition("q0",'a',"q1");
        A.addTransition("q0",'b',"q2");
        A.addTransition("q1",'a',"q2");
        A.addTransition("q1",'b',"q2");
        A.addTransition("q2",'a',"q2");
        A.addTransition("q2",'b',"q2");

        A.addFinalStates("q1");

        Assertions.assertEquals(true,A.accept("a"));
        Assertions.assertEquals(false,A.accept("aaaaaa"));

        Dfa kleeneStartDfa = (Dfa)A.kleeneStar();
        Assertions.assertEquals(true,kleeneStartDfa.accept("aaaaaa"));
    }

    @Test
    /**
     * Automata accepts words in form (a^n)b
     * It's complement should accept everything else
     */
    void complement() {
        Dfa A1 = new Dfa("q0");
        A1.addTransition("q0",'a',"q0");
        A1.addTransition("q0",'b',"q1");
        A1.addTransition("q1",'a',"q2");
        A1.addTransition("q1",'b',"q2");
        A1.addTransition("q2",'a',"q2");
        A1.addTransition("q2",'b',"q2");
        A1.addFinalStates("q1");

        Assertions.assertEquals(true,A1.accept("aaab"));
        Assertions.assertEquals(false,A1.accept("aaaabbbbaa"));

        Dfa complementDfa = (Dfa) A1.complement();

        Assertions.assertEquals(false,complementDfa.accept("aaab"));
        Assertions.assertEquals(true,complementDfa.accept("aaaabbbbaa"));
        Assertions.assertEquals(true,complementDfa.accept("bbbbb"));
        Assertions.assertEquals(true,complementDfa.accept("aba"));

    }

    @Test
    /**
     * First automata represents language defined with (a*)b - non-final
     * Second automata represents language defined with ab - final
     */
    void shouldCheckIfLanguageIsFinal() {

        Dfa first = new Dfa("q0");
        first.addTransition("q0",'a',"q0");
        first.addTransition("q0",'b',"q1");
        first.addTransition("q1",'a',"q2");
        first.addTransition("q1",'b',"q2");
        first.addTransition("q2",'a',"q2");
        first.addTransition("q2",'b',"q2");

        first.addFinalStates("q1");

        Assertions.assertEquals(false,first.finality());

        Dfa second = new Dfa("q0");

        second.addTransition("q0",'a',"q1");
        second.addTransition("q0",'b',"q3");
        second.addTransition("q1",'a',"q3");
        second.addTransition("q1",'b',"q2");
        second.addTransition("q2",'a',"q3");
        second.addTransition("q2",'b',"q3");
        second.addTransition("q3",'a',"q3");
        second.addTransition("q3",'b',"q3");

        second.addFinalStates("q2");

        Assertions.assertEquals(true,second.finality());

    }
}