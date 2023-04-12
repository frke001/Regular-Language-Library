package org.unibl.etf.regularExpression;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.unibl.etf.automata.*;
import org.unibl.etf.exceptions.DuplicateTransitionException;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class RegularExpressionTest {

    @Test
    void accept() {
        RegularExpression regex = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        regex.setRegex("a*.b.b.b*");

        Assertions.assertEquals(true, regex.accept("bb"));
        Assertions.assertEquals(true, regex.accept("aaaabbbbb"));
        Assertions.assertEquals(false, regex.accept("ab"));
    }

    @Test
    void toENfa() {

        RegularExpression regex = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        regex.setRegex("a.b|a*");
        Assertions.assertEquals(true, regex.accept("ab"));
        Assertions.assertEquals(true, regex.accept("aaaaaaa"));
        Assertions.assertEquals(false, regex.accept("abb"));

        EpsilonNfa epsilonNfa = regex.toENfa();

        Assertions.assertEquals(true, epsilonNfa.accept("ab"));
        Assertions.assertEquals(true, epsilonNfa.accept("aaaaaaa"));
        Assertions.assertEquals(false, epsilonNfa.accept("abb"));
    }

    @Test
    void toDfa() {
        RegularExpression regex = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        regex.setRegex("a.b|a*|a.b.b.a");
        Assertions.assertEquals(true, regex.accept("ab"));
        Assertions.assertEquals(true, regex.accept("aaaaaaa"));
        Assertions.assertEquals(true, regex.accept("abba"));
        Assertions.assertEquals(false, regex.accept("abb"));

        Dfa dfa = regex.toDfa();

        Assertions.assertEquals(true, dfa.accept("ab"));
        Assertions.assertEquals(true, dfa.accept("aaaaaaa"));
        Assertions.assertEquals(true, dfa.accept("abba"));
        Assertions.assertEquals(false, dfa.accept("abb"));
    }

    @Test
    void equality() {

        RegularExpression regex = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        regex.setRegex("a.b.b.a");

        EpsilonNfa epsilonNfa = regex.toENfa();
        Dfa dfa = regex.toDfa();

        EpsilonNfa epsilonNfa1 = new EpsilonNfa("q0");
        epsilonNfa1.addTransition("q0", 'a', new String[]{"q1"});
        epsilonNfa1.addTransition("q1", 'b', new String[]{"q2"});
        epsilonNfa1.addTransition("q2", 'b', new String[]{"q3"});
        epsilonNfa1.addTransition("q3", 'a', new String[]{"q4"});
        epsilonNfa1.addFinalStates("q4");


        Assertions.assertEquals(true, regex.equality(epsilonNfa));
        Assertions.assertEquals(true, regex.equality(epsilonNfa1));
        Assertions.assertEquals(true, regex.equality(dfa));
        Assertions.assertEquals(true, epsilonNfa.equality(epsilonNfa1));
        Assertions.assertEquals(true, dfa.equality(epsilonNfa1));
        Assertions.assertEquals(true, epsilonNfa.equality(dfa));

        RegularExpression regex1 = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        regex1.setRegex("a*");

        Assertions.assertEquals(false, regex1.equality(epsilonNfa));
        Assertions.assertEquals(false, regex1.equality(epsilonNfa1));
        Assertions.assertEquals(false, regex1.equality(dfa));

    }

    @Test
    void union() {

        RegularExpression first = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        first.setRegex("a.b");
        RegularExpression second = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        second.setRegex("a*");
        Assertions.assertEquals(true, first.accept("ab"));
        Assertions.assertEquals(true, second.accept("aaaaaaa"));
        Assertions.assertEquals(false, second.accept("ab"));

        RegularExpression union = (RegularExpression) first.union(second);

        Assertions.assertEquals(true, union.accept("ab"));
        Assertions.assertEquals(true, union.accept("aaaaaaa"));

    }

    @Test
    void intersection() {
        RegularExpression first = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        first.setRegex("a.b|a");
        RegularExpression second = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        second.setRegex("a*|a.b");
        Assertions.assertEquals(true, first.accept("ab"));
        Assertions.assertEquals(true, second.accept("aaaaaaa"));
        Assertions.assertEquals(true, second.accept("ab"));

        Dfa intersection = (Dfa) first.intersection(second);

        Assertions.assertEquals(true, intersection.accept("ab"));
        Assertions.assertEquals(false, intersection.accept("aaaaaaa"));
        Assertions.assertEquals(false, intersection.accept("bbbb"));
    }

    @Test
    void difference() {
        RegularExpression first = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        first.setRegex("a.b|a|a.a");
        RegularExpression second = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        second.setRegex("b|a.a");
        Assertions.assertEquals(true, first.accept("ab"));
        Assertions.assertEquals(false, second.accept("aba"));
        Assertions.assertEquals(true, second.accept("aa"));
        Assertions.assertEquals(true, second.accept("b"));

        Dfa difference = (Dfa) first.difference(second);

        Assertions.assertEquals(true, difference.accept("ab"));
        Assertions.assertEquals(true, difference.accept("a"));
        Assertions.assertEquals(false, difference.accept("aa"));
        Assertions.assertEquals(false, difference.accept("b"));

    }

    @Test
    void concatenation() {
        RegularExpression first = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        first.setRegex("a.b");
        RegularExpression second = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        second.setRegex("a*");
        Assertions.assertEquals(true, first.accept("ab"));
        Assertions.assertEquals(true, second.accept("aaaaaa"));

        RegularExpression concatenation = (RegularExpression) first.concatenation(second);

        Assertions.assertEquals(true, concatenation.accept("ab"));
        Assertions.assertEquals(true, concatenation.accept("abaaa"));
        Assertions.assertEquals(false, concatenation.accept("abbaaa"));
    }

    @Test
    void kleeneStar() {
        RegularExpression first = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        first.setRegex("a.b*");

        RegularExpression kleeneStar = (RegularExpression) first.kleeneStar();

        Assertions.assertEquals(true, kleeneStar.accept("ab"));
        Assertions.assertEquals(true, kleeneStar.accept("abbb"));
        Assertions.assertEquals(true, kleeneStar.accept("ababbb"));
        Assertions.assertEquals(true, kleeneStar.accept("abababbb"));
    }

    @Test
    void complement() {
        RegularExpression first = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        first.setRegex("a.b*");

        Assertions.assertEquals(true, first.accept("ab"));
        Assertions.assertEquals(true, first.accept("abbb"));

        Dfa complement = (Dfa) first.complement();

        Assertions.assertEquals(false, complement.accept("ab"));
        Assertions.assertEquals(false, complement.accept("abbb"));
        Assertions.assertEquals(true, complement.accept("bbbb"));
        Assertions.assertEquals(true, complement.accept("baba"));
    }

    @Test
    void maximumLengthWord() {
        RegularExpression first = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        first.setRegex("a.b|a.b.a|a.a.b.b");
        RegularExpression second = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        second.setRegex("a*.a.b");

        Assertions.assertEquals(4, first.maximumLengthWord());
        Assertions.assertEquals(-1, second.maximumLengthWord());
    }

    @Test
    void minimumLengthWord() {
        RegularExpression first = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        first.setRegex("a.b|a.b.a|a.a.b.b");
        RegularExpression second = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        second.setRegex("a*");

        Assertions.assertEquals(2, first.minimumLengthWord());
        Assertions.assertEquals(0, second.minimumLengthWord());

    }

    @Test
    void finality() {
        RegularExpression first = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        first.setRegex("a.b|a.b.a|a.a.b.b");
        RegularExpression second = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        second.setRegex("a*.a.b");

        Assertions.assertEquals(true, first.finality());
        Assertions.assertEquals(false, second.finality());
    }

    @Test
    /**
     * Result (a.b.b)*.a
     */
    void chainingOperations() {

        RegularExpression first = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        first.setRegex("a.b");
        RegularExpression second = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        second.setRegex("b");
        RegularExpression third = new RegularExpression(new HashSet<>() {{
            add('a');
            add('b');
        }});
        third.setRegex("a");

        RegularExpression chaningOperatios = (RegularExpression) ((first.concatenation(second)).kleeneStar()).concatenation(third);

        Assertions.assertEquals(true, chaningOperatios.accept("abbabba"));
        Assertions.assertEquals(true, chaningOperatios.accept("a"));
    }
}