package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.type.Type;

/**
 * Created by Dawid on 20.07.2019 at 15:55.
 */
public class CollectionClassSymbol extends ClassSymbol {

    public CollectionClassSymbol(Type variableType) {
        super(variableType);
    }

    public CollectionClassSymbol(String name) {
        super(name);
    }

    public CollectionClassSymbol(String text, Type VariableType) {
        super(text, VariableType);
    }
}
