package com.dawid.typepython.symtab.symbol.embeded;

import com.dawid.typepython.symtab.symbol.CollectionClassSymbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.symbol.type.VariableType;

/**
 * Created by Dawid on 11.09.2019 at 21:23.
 */
public class ListSymbol extends CollectionClassSymbol {
    public ListSymbol(VariableType variableType) {
        super(variableType);
    }

    public ListSymbol(String name) {
        super(name);
    }

    public ListSymbol(VariableType variableType, TypedSymbol nestedSymbol) {
        super(variableType, nestedSymbol);
    }

    public ListSymbol(String text, VariableType VariableType, TypedSymbol nestedType) {
        super(text, VariableType, nestedType);
    }

    public TypedSymbol getElement(TypedSymbol argument) {
        if (!argument.getVariableType().isNumeric()) {
            throw new IllegalArgumentException(argument.toString());
        }

        return getNested();
    }
}
