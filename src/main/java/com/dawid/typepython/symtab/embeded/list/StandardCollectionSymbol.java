package com.dawid.typepython.symtab.embeded.list;

import com.dawid.typepython.symtab.symbol.CollectionClassSymbol;
import com.dawid.typepython.symtab.type.Type;

/**
 * Created by Dawid on 11.09.2019 at 21:23.
 */
public class StandardCollectionSymbol extends CollectionClassSymbol {
    public static final String GENERIC_TEMPLATE_NAME = "E";

    public StandardCollectionSymbol(Type variableType) {
        super(variableType);
    }

    public StandardCollectionSymbol(String name) {
        super(name);
    }

    public StandardCollectionSymbol(String text, Type variableType) {
        super(text, variableType);
    }
}
