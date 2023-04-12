package org.unibl.etf.specification;

public class Token {

    private String type;
    private String semanticValue;

    public Token(String type,String semanticValue){
        this.type = type;
        this.semanticValue = semanticValue;
    }
    public String getType(){
        return type;
    }
    public String getSemanticValue(){
        return semanticValue;
    }
}