package com.dawid.typepython.symtab.type;

import com.dawid.typepython.symtab.symbol.MethodSymbol;

import java.util.ArrayList;
import java.util.List;

public interface Type extends TypeMatcher {
    String getCppNameType();

    boolean isCollection();

    default boolean isGenericType() {
        return false;
    }

    boolean isNumeric();

    String getPythonType();

    default List<MethodSymbol> getMethodSymbol() {
        return new ArrayList<>();
    }
}
