package org.unibl.etf.codeGenerator;

import org.unibl.etf.addition.Pair;
import org.unibl.etf.automata.Dfa;

public class CodeGenerator {
    private Dfa dfa;

    public CodeGenerator(Dfa dfa) {
        this.dfa = dfa;
    }

    private String generateAcceptMethod() {
        StringBuilder generatedString = new StringBuilder();
        generatedString.append("\tpublic boolean accept(String input, Reactions enteringState, Reactions exitingState");
        for (var symbol : dfa.getAlphabet()) {
            generatedString.append(", Reactions symbol" + symbol);
        }
        generatedString.append(")\n\t{\n");
        generatedString.append("\t\tString currState = new String(\"" + dfa.getStartState() + "\");\n");
        generatedString.append("\t\tfor(var symbol : input.toCharArray())\n\t\t{\n");
        generatedString.append("\t\t\tswitch (currState)\n\t\t\t{\n");
        for (var state : dfa.getStates()) {
            generatedString.append("\t\t\t\tcase \"" + state + "\":\n");
            generatedString.append("\t\t\t\t\texitingState.react(currState);\n");
            for (var symbol : dfa.getAlphabet()) {
                generatedString.append("\t\t\t\t\tif (symbol == '" + symbol + "')\n");
                generatedString.append("\t\t\t\t\t{\n");
                generatedString.append("\t\t\t\t\t\tsymbol" + symbol + ".react(currState);\n");
                generatedString.append("\t\t\t\t\t\tcurrState = \"" + dfa.getDelta().get(new Pair<>(state, symbol)) + "\";\n");
                generatedString.append("\t\t\t\t\t}\n");
            }
            generatedString.append("\t\t\t\t\tenteringState.react(currState);\n");
            generatedString.append("\t\t\t\t\tbreak;\n");
        }
        generatedString.append("\t\t\t}\n");
        generatedString.append("\t\t}\n");
        generatedString.append("\t\treturn ");
        int br = 0;
        for (var finalState : dfa.getFinalStates()) {
            if (br == 0)
                generatedString.append("currState.equals(\"" + finalState + "\")");
            else {
                br++;
                generatedString.append(" || currState.equals(\"" + finalState + "\")");
            }
        }
        generatedString.append(";\n");
        generatedString.append("\t}\n");
        return generatedString.toString();
    }
    public String codeGenerator(){
        StringBuilder generatorString = new StringBuilder();
        generatorString.append("import java.util.LinkedList;\n");
        generatorString.append("import java.util.function.Consumer;\n");
        generatorString.append("\n");
        generatorString.append("public class AutomataDFA\n{\n");
        generatorString.append(this.generateAcceptMethod());
        generatorString.append("}\n");
        generatorString.append("class Reactions\n{\n");
        generatorString.append("\tpublic LinkedList<Consumer<String>> reactions = new LinkedList<>();\n");
        generatorString.append("\tpublic void react(String state)\n\t{\n");
        generatorString.append("\t\treactions.forEach(value -> {\n");
        generatorString.append("\t\t\tvalue.accept(state);\n");
        generatorString.append("\t\t});\n");
        generatorString.append("\t}\n");
        generatorString.append("}\n");
        return generatorString.toString();
    }
}
