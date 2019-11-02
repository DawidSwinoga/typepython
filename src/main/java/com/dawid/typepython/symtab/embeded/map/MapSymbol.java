package com.dawid.typepython.symtab.embeded.map;

import com.dawid.typepython.symtab.symbol.CollectionClassSymbol;
import com.dawid.typepython.symtab.type.Type;

/**
 * Created by Dawid on 02.11.2019 at 11:04.
 */
public class MapSymbol extends CollectionClassSymbol {
    public static final String KEY_TEMPLATE = "K";
    public static final String VALUE_TEMPLATE = "V";

    public MapSymbol(Type variableType) {
        super(variableType);
    }

    public MapSymbol(String name) {
        super(name);
    }

    public MapSymbol(String text, Type VariableType) {
        super(text, VariableType);
    }
}
