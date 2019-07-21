package com.dawid.typepython.symtab.symbol.type;

public interface VariableType {
    String getCppNameType();
    boolean isCollection();
    boolean isNumeric();
    String getPythonType();
}
