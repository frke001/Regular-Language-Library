package org.unibl.etf.program;

import org.unibl.etf.automata.Dfa;
import org.unibl.etf.codeGenerator.CodeGenerator;
import org.unibl.etf.specification.SpecificationAnalyzer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Program {
    public static void main(String args[]){
        StringBuilder input = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get("specification.txt"));
            for(var el : lines){
                input.append(el);
                input.append("\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        SpecificationAnalyzer specificationAnalyzer = new SpecificationAnalyzer(input.toString());
        Dfa automat =(Dfa) specificationAnalyzer.acceptance().toDfa();

        CodeGenerator cd = new CodeGenerator(automat);
        PrintWriter pr = null;
        try {
            pr = new PrintWriter(new FileWriter("automat2.java"));
            pr.println(cd.codeGenerator());
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(pr != null)
                pr.close();
        }
    }
}
