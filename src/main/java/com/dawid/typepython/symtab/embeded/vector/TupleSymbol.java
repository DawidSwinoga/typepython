package com.dawid.typepython.symtab.embeded.vector;

import com.dawid.typepython.symtab.symbol.CollectionClassSymbol;
import com.dawid.typepython.symtab.type.Type;

/**
 * Created by Dawid on 15.10.2019 at 20:39.
 */
public class TupleSymbol extends CollectionClassSymbol {
    public static final String GENERIC_TEMPLATE_NAME = "E";

    public TupleSymbol(Type variableType) {
        super(variableType);
    }

    public TupleSymbol(String name) {
        super(name);
    }

    public TupleSymbol(String text, Type VariableType) {
        super(text, VariableType);
    }
}
