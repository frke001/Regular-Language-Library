package org.unibl.etf.automata;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.unibl.etf.exceptions.DuplicateTransitionException;
import org.unibl.etf.regularExpression.RegularExpression;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class EpsilonNfaTest {

    @Test
    @DisplayName("Checking if ENFA accepts input string.")
    void shouldAcceptInputString() {
        EpsilonNfa  E1 = new EpsilonNfa("q0");
        E1.addTransition("q0", 'b', new String[]{"q1"});
        E1.addTransition("q1", 'a', new String[]{"q2"});
        E1.addTransition("q1", 'b', new String[]{"q2"});
        E1.addTransition("q1", EpsilonNfa.EPSILON , new String[]{"q5"});
        E1.addTransition("q2", 'a', new String[]{"q5"});
        E1.addTransition("q2", 'b', new String[]{"q3"});
        E1.addTransition("q2", EpsilonNfa.EPSILON , new String[]{"q6"});
        E1.addTransition("q3", 'a', new String[]{"q6"});
        E1.addTransition("q4", 'b', new String[]{"q0"});
        E1.addTransition("q5", 'a', new String[]{"q0","q4"});
        E1.addTransition("q6", EpsilonNfa.EPSILON, new String[]{"q5"});

        E1.addFinalStates("q6");

        Assertions.assertEquals(true,E1.accept("bbba"));
        Assertions.assertEquals(true,E1.accept("ba"));
        Assertions.assertEquals(false,E1.accept("b"));
        Assertions.assertEquals(false,E1.accept("bbaa"));
    }

    @Test
    @DisplayName("Adding existing transition.")
    void shouldThrowDuplicateTransitionExceptionWhenAddingExistingTransition(){
        EpsilonNfa  DuplicateTransition = new EpsilonNfa("q0");
        DuplicateTransition.addTransition("q0", 'b', new String[]{"q1"});
        DuplicateTransition.addTransition("q1", 'a', new String[]{"q2"});
        DuplicateTransition.addTransition("q1", 'b', new String[]{"q2"});

        Assertions.assertThrows(DuplicateTransitionException.class, ()-> {
            DuplicateTransition.addTransition("q1", 'b', new String[]{"q2"});
        });
    }
    @Test
    @DisplayName("Transforming ENFA to DFA")
    void toDfa() {
        EpsilonNfa E2 = new EpsilonNfa("q0");

        E2.addTransition("q0", EpsilonNfa.EPSILON, new String[]{"q1"});
        E2.addTransition("q0", 'a', new String[]{"q0"});
        E2.addTransition("q1", EpsilonNfa.EPSILON, new String[]{"q0"});
        E2.addTransition("q1", 'b', new String[]{"q2","q4"});
        E2.addTransition("q2", 'a', new String[]{"q2","q3"});
        E2.addTransition("q3", EpsilonNfa.EPSILON, new String[]{"q1"});
        E2.addTransition("q3", 'b', new String[]{"q3"});
        E2.addTransition("q4", 'a', new String[]{"q1"});
        E2.addFinalStates("q1");

        String input = "";
        input += EpsilonNfa.EPSILON;
        Assertions.assertEquals(true,E2.accept(input));
        Assertions.assertEquals(true,E2.accept("aaaaababbbb"));

        Dfa transformToDfa = E2.toDfa();

        Assertions.assertEquals(true,transformToDfa.accept("aaaaababbbb"));
    }

    @Test
    /**
     * Closure of StartState contains final state that means that minimum length word is 0
     */
    void minimumLengthWord() {
        EpsilonNfa E2 = new EpsilonNfa("q0");

        E2.addTransition("q0", EpsilonNfa.EPSILON, new String[]{"q1"});
        E2.addTransition("q0", 'a', new String[]{"q0"});
        E2.addTransition("q1", EpsilonNfa.EPSILON, new String[]{"q0"});
        E2.addTransition("q1", 'b', new String[]{"q2","q4"});
        E2.addTransition("q2", 'a', new String[]{"q2","q3"});
        E2.addTransition("q3", EpsilonNfa.EPSILON, new String[]{"q1"});
        E2.addTransition("q3", 'b', new String[]{"q3"});
        E2.addTransition("q4", 'a', new String[]{"q1"});
        E2.addFinalStates("q1");

        for(var state : E2.closure("q0")){
            System.out.print(state + " ");
        }

        Assertions.assertEquals(0,E2.minimumLengthWord());
        Assertions.assertFalse(E2.minimumLengthWord() == 1);
    }

    @Test
    void shouldAcceptWordsFromBothLanguagesRepresentedByThisDFA(){
        EpsilonNfa E3 = new EpsilonNfa("q0");
        E3.addTransition("q0", EpsilonNfa.EPSILON, new String[]{"q1","q2"});
        E3.addTransition("q1", 'a', new String[]{"q3"});
        E3.addTransition("q2", 'b', new String[]{"q3"});
        E3.addTransition("q3", 'b', new String[]{"q4"});
        E3.addFinalStates("q4");

        Assertions.assertEquals(true,E3.accept("ab"));

        EpsilonNfa E4 = new EpsilonNfa("q5");
        E4.addTransition("q5", 'a', new String[]{"q6"});
        E4.addTransition("q5", 'b', new String[]{"q5"});
        E4.addTransition("q6", 'b', new String[]{"q6"});
        E4.addTransition("q6", EpsilonNfa.EPSILON, new String[]{"q7","q9"});
        E4.addTransition("q7", 'a', new String[]{"q7"});
        E4.addTransition("q7", 'b', new String[]{"q10"});
        E4.addTransition("q8", 'a', new String[]{"q9"});
        E4.addTransition("q9", 'a', new String[]{"q10"});
        E4.addTransition("q9", 'b', new String[]{"q8"});
        E4.addFinalStates("q10");

        Assertions.assertEquals(true,E4.accept("ababaa"));
        Assertions.assertEquals(true,E4.accept("abbbb"));
        Assertions.assertEquals(false,E4.accept("bb"));

        EpsilonNfa unionNfa = (EpsilonNfa)E3.union(E4);

        Assertions.assertEquals(true,unionNfa.accept("ab"));
        Assertions.assertEquals(true,unionNfa.accept("abbbb"));
        Assertions.assertEquals(true,unionNfa.accept("bb"));

    }

    @Test
    /**
     * First accepts word ab,so does second, but firs accepts bb and second does not
     * Intersection is word ab, not bb
     */
    void shouldAcceptOnlyWordsThatAreAcceptedFromBothAutomata() {
        EpsilonNfa E3 = new EpsilonNfa("q0");
        E3.addTransition("q0", EpsilonNfa.EPSILON, new String[]{"q1","q2"});
        E3.addTransition("q1", 'a', new String[]{"q3"});
        E3.addTransition("q2", 'b', new String[]{"q3"});
        E3.addTransition("q3", 'b', new String[]{"q4"});
        E3.addFinalStates("q4");

        Assertions.assertEquals(true,E3.accept("ab"));

        EpsilonNfa E4 = new EpsilonNfa("q5");
        E4.addTransition("q5", 'a', new String[]{"q6"});
        E4.addTransition("q5", 'b', new String[]{"q5"});
        E4.addTransition("q6", 'b', new String[]{"q6"});
        E4.addTransition("q6", EpsilonNfa.EPSILON, new String[]{"q7","q9"});
        E4.addTransition("q7", 'a', new String[]{"q7"});
        E4.addTransition("q7", 'b', new String[]{"q10"});
        E4.addTransition("q8", 'a', new String[]{"q9"});
        E4.addTransition("q9", 'a', new String[]{"q10"});
        E4.addTransition("q9", 'b', new String[]{"q8"});
        E4.addFinalStates("q10");

        Assertions.assertEquals(true,E4.accept("ababaa"));
        Assertions.assertEquals(true,E4.accept("abbbb"));
        Assertions.assertEquals(false,E4.accept("bb"));

        EpsilonNfa intersectionNfa = (EpsilonNfa)E3.intersection(E4);

        Assertions.assertEquals(true,intersectionNfa.accept("ab"));
        Assertions.assertEquals(false,intersectionNfa.accept("abbbb"));
        Assertions.assertEquals(false,intersectionNfa.accept("bb"));
    }

    @Test
    /**
     * First accepts bb and second does not so that is word that accepts automata represented by operation difference
     */
    void shouldAcceptOnlyWordsThatAreAcceptedByFirstAutomataNotBySecond() {
        EpsilonNfa E3 = new EpsilonNfa("q0");
        E3.addTransition("q0", EpsilonNfa.EPSILON, new String[]{"q1","q2"});
        E3.addTransition("q1", 'a', new String[]{"q3"});
        E3.addTransition("q2", 'b', new String[]{"q3"});
        E3.addTransition("q3", 'b', new String[]{"q4"});
        E3.addFinalStates("q4");

        Assertions.assertEquals(true,E3.accept("ab"));

        EpsilonNfa E4 = new EpsilonNfa("q5");
        E4.addTransition("q5", 'a', new String[]{"q6"});
        E4.addTransition("q5", 'b', new String[]{"q5"});
        E4.addTransition("q6", 'b', new String[]{"q6"});
        E4.addTransition("q6", EpsilonNfa.EPSILON, new String[]{"q7","q9"});
        E4.addTransition("q7", 'a', new String[]{"q7"});
        E4.addTransition("q7", 'b', new String[]{"q10"});
        E4.addTransition("q8", 'a', new String[]{"q9"});
        E4.addTransition("q9", 'a', new String[]{"q10"});
        E4.addTransition("q9", 'b', new String[]{"q8"});
        E4.addFinalStates("q10");

        Assertions.assertEquals(true,E4.accept("ababaa"));
        Assertions.assertEquals(true,E4.accept("abbbb"));
        Assertions.assertEquals(false,E4.accept("bb"));

        EpsilonNfa differenceNfa = (EpsilonNfa)E3.difference(E4);

        Assertions.assertEquals(false,differenceNfa.accept("ab"));
        Assertions.assertEquals(false,differenceNfa.accept("abbbb"));
        Assertions.assertEquals(true,differenceNfa.accept("bb"));
    }

    @Test
    /**
     * This EpsilonNfa represents language given by a*bb*
     */
    void equality() {
        EpsilonNfa epsilonNfa = new EpsilonNfa("q0");

        epsilonNfa.addTransition("q0", 'a', new String[]{"q0"});
        epsilonNfa.addTransition("q0", 'b', new String[]{"q1"});
        epsilonNfa.addTransition("q1", 'b', new String[]{"q1"});
        epsilonNfa.addFinalStates("q1");

        Assertions.assertEquals(true,epsilonNfa.accept("abb"));
        Assertions.assertEquals(true,epsilonNfa.accept("aaaabbbb"));
        Assertions.assertEquals(true,epsilonNfa.accept("b"));
        Assertions.assertEquals(true,epsilonNfa.accept("ab"));
        Assertions.assertEquals(false,epsilonNfa.accept("aaaa"));

        Dfa transformDfa = epsilonNfa.toDfa();
        Assertions.assertEquals(true,transformDfa.accept("abb"));
        Assertions.assertEquals(true,transformDfa.accept("aaaabbbb"));
        Assertions.assertEquals(true,transformDfa.accept("b"));
        Assertions.assertEquals(true,transformDfa.accept("ab"));
        Assertions.assertEquals(false,transformDfa.accept("aaaa"));

        Assertions.assertEquals(true,transformDfa.equality(epsilonNfa));

        RegularExpression regex = new RegularExpression(new HashSet<>(){{add('a'); add('b');}});
        regex.setRegex("a*.b.b*");

        Assertions.assertEquals(true,regex.accept("abb"));
        Assertions.assertEquals(true,regex.accept("aaaabbbb"));
        Assertions.assertEquals(true,regex.accept("b"));
        Assertions.assertEquals(true,regex.accept("ab"));
        Assertions.assertEquals(false,regex.accept("aaaa"));

        Assertions.assertEquals(true,epsilonNfa.equality(regex));
        Assertions.assertEquals(true,epsilonNfa.equality(epsilonNfa));

    }

    @Test
    /**
     * First - a*bb*
     * Second - a*
     */
    void concatenation() {

        EpsilonNfa first = new EpsilonNfa("q0");

        first.addTransition("q0", 'a', new String[]{"q0"});
        first.addTransition("q0", 'b', new String[]{"q1"});
        first.addTransition("q1", 'b', new String[]{"q1"});
        first.addFinalStates("q1");

        Assertions.assertEquals(true,first.accept("abb"));
        Assertions.assertEquals(true,first.accept("aaabbbb"));
        Assertions.assertEquals(false,first.accept("abbaa"));

        EpsilonNfa second = new EpsilonNfa("q0");

        second.addTransition("q0", 'a', new String[]{"q0"});
        second.addTransition("q0", 'b', new String[]{"q1"});
        second.addFinalStates("q0");

        Assertions.assertEquals(false,second.accept("abb"));
        Assertions.assertEquals(true,second.accept("aaa"));

        EpsilonNfa concatenationNfa = (EpsilonNfa) first.concatenation(second);

        Assertions.assertEquals(true,concatenationNfa.accept("abb"));
        Assertions.assertEquals(true,concatenationNfa.accept("aaa"));
        Assertions.assertEquals(true,concatenationNfa.accept("aaabbbbaaaa"));
    }

    @Test
    /**
     * Automata represents language given by aab
     * kleeneStar is (aab)*
     */
    void kleeneStar() {
        EpsilonNfa first = new EpsilonNfa("q0");

        first.addTransition("q0", 'a', new String[]{"q1"});
        first.addTransition("q1", 'a', new String[]{"q2"});
        first.addTransition("q2", 'b', new String[]{"q3"});
        first.addFinalStates("q3");

        Assertions.assertEquals(true,first.accept("aab"));
        Assertions.assertEquals(false,first.accept("aabaab"));

        EpsilonNfa kleeneStartNfa =(EpsilonNfa) first.kleeneStar();

        Assertions.assertEquals(true,kleeneStartNfa.accept("&"));
        Assertions.assertEquals(true,kleeneStartNfa.accept("aabaab"));
        Assertions.assertEquals(true,kleeneStartNfa.accept("aab"));
        Assertions.assertEquals(true,kleeneStartNfa.accept("aabaabaab"));

    }

    @Test
    /**
     * Automata represents language given by aa*b
     */
    void complement() {
        EpsilonNfa first = new EpsilonNfa("q0");

        first.addTransition("q0", 'a', new String[]{"q1"});
        first.addTransition("q1", 'a', new String[]{"q1"});
        first.addTransition("q1", 'b', new String[]{"q2"});
        first.addFinalStates("q2");

        Assertions.assertEquals(true,first.accept("aaaab"));
        Assertions.assertEquals(true,first.accept("ab"));
        Assertions.assertEquals(false,first.accept("aaaa"));

        EpsilonNfa complementNfa = (EpsilonNfa) first.complement();

        Assertions.assertEquals(false,complementNfa.accept("aaaab"));
        Assertions.assertEquals(false,complementNfa.accept("ab"));
        Assertions.assertEquals(true,complementNfa.accept("aaaa"));
    }

    @Test
    /**
     * First Automata represents language given by ab*b
     * Second Automata represents language given by abb
     */
    void maximumLengthWord() {

        EpsilonNfa first = new EpsilonNfa("q0");
        first.addTransition("q0", 'a', new String[]{"q1"});
        first.addTransition("q1", 'b', new String[]{"q1","q2"});
        first.addFinalStates("q2");

        Assertions.assertEquals(true,first.accept("abbbb"));
        Assertions.assertEquals(true,first.accept("ab"));
        Assertions.assertEquals(false,first.accept("aaaa"));

        EpsilonNfa second = new EpsilonNfa("q0");

        second.addTransition("q0", 'a', new String[]{"q1"});
        second.addTransition("q1", 'b', new String[]{"q2"});
        second.addTransition("q2", 'b', new String[]{"q3"});
        second.addFinalStates("q3");

        Assertions.assertEquals(true,second.accept("abb"));
        Assertions.assertEquals(false,second.accept("ab"));
        Assertions.assertEquals(false,second.accept("aaaa"));


        Assertions.assertEquals(-1,first.maximumLengthWord());
        Assertions.assertEquals(3,second.maximumLengthWord());

    }

    @Test
    void finality() {
        EpsilonNfa first = new EpsilonNfa("q0");
        first.addTransition("q0", 'a', new String[]{"q1"});
        first.addTransition("q1", 'b', new String[]{"q1","q2"});
        first.addFinalStates("q2");

        Assertions.assertEquals(true,first.accept("abbbb"));
        Assertions.assertEquals(true,first.accept("ab"));
        Assertions.assertEquals(false,first.accept("aaaa"));

        EpsilonNfa second = new EpsilonNfa("q0");

        second.addTransition("q0", 'a', new String[]{"q1"});
        second.addTransition("q1", 'b', new String[]{"q2"});
        second.addTransition("q2", 'b', new String[]{"q3"});
        second.addFinalStates("q3");

        Assertions.assertEquals(true,second.accept("abb"));
        Assertions.assertEquals(false,second.accept("ab"));
        Assertions.assertEquals(false,second.accept("aaaa"));


        Assertions.assertEquals(false,first.finality());
        Assertions.assertEquals(true,second.finality());
    }
}