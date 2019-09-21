package com.dawid.typepython.symtab.embeded.list;

import com.dawid.typepython.symtab.symbol.CollectionClassSymbol;
import com.dawid.typepython.symtab.type.Type;

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
}
