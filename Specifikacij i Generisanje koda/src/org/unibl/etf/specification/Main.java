package org.unibl.etf.specification;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String args[]){

        StringBuilder input = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get("./src/org/unibl/etf/specification/specification1.txt"));
            for(var el : lines){
                input.append(el);
                input.append("\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        SpecificationAnalyzer specificationAnalyzer = new SpecificationAnalyzer(input.toString());
        specificationAnalyzer.acceptance();
    }
}

