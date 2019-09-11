package com.dawid.typepython.symtab.symbol.type;

public interface VariableType extends TypeMatcher {
    String getCppNameType();

    boolean isCollection();

    default boolean isGenericType() {
        return false;
    }

    boolean isNumeric();

    String getPythonType();
}
