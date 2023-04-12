package org.unibl.etf.exceptions;


public class DuplicateTransitionException extends RuntimeException {
    public DuplicateTransitionException(){
        super();
    }
    public  DuplicateTransitionException(String message){
        super(message);
    }
}
