package org.unibl.etf.interfaces;

public interface IRegularLanguage {
    IRegularLanguage toDfa();
    IRegularLanguage toENfa();
    boolean equality(IRegularLanguage other);
    IRegularLanguage union(IRegularLanguage other);
    IRegularLanguage intersection(IRegularLanguage other);
    IRegularLanguage difference(IRegularLanguage other);
    IRegularLanguage concatenation(IRegularLanguage other);
    IRegularLanguage kleeneStar();
    IRegularLanguage complement();
    int minimumLengthWord();
    int maximumLengthWord();
    boolean finality();

}