package com.dawid.typepython.symtab.embeded.list;

import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.symbol.CollectionClassSymbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.symbol.VariableTypeMissmatchException;
import com.dawid.typepython.symtab.type.Type;
import type.CppVariableType;

/**
 * Created by Dawid on 11.09.2019 at 21:23.
 */
public class ListSymbol extends CollectionClassSymbol {
    public static final String GENERIC_TEMPLATE_NAME = "E";

    public ListSymbol(Type variableType) {
        super(variableType);
    }

    public ListSymbol(String name) {
        super(name);
    }

    public ListSymbol(String text, Type variableType) {
        super(text, variableType);
    }

    public TypedSymbol getElement(TypedSymbol argument) {
        throw new UnsupportedOperationException();
//        if (!argument.getVariableType().isNumeric()) {
//            throw new IllegalArgumentException(argument.toString());
//        }
//
//        return getNested();
    }

    public TypedSymbol append(TypedSymbol typedSymbol) {
        throw new UnsupportedOperationException();
//        MatchType match = getNested().match(typedSymbol);
//
//        if (match == MatchType.NONE) {
//            throw new VariableTypeMissmatchException();
//        }
//
//        return new TypedSymbol(".push_back(" + typedSymbol.getText() + ")", CppVariableType.VOID);
    }
}
