package com.dawid.typepython.symtab.symbol.type;

public interface VariableType extends TypeMatcher {
    String getCppNameType();
    boolean isCollection();
    boolean isNumeric();
    String getPythonType();
}
