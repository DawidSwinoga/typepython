package com.dawid.typepython.symtab.type;

public interface Type extends TypeMatcher {
    String getCppNameType();

    boolean isCollection();

    default boolean isGenericType() {
        return false;
    }

    boolean isNumeric();

    String getPythonType();
}
