package com.dawid.typepython.symtab.embeded.list;

import com.dawid.typepython.TokenSymbolInfo;
import com.dawid.typepython.symtab.symbol.CollectionClassSymbol;
import com.dawid.typepython.symtab.type.Type;

/**
 * Created by Dawid on 11.09.2019 at 21:23.
 */
public class StandardCollectionSymbol extends CollectionClassSymbol {
    public static final String GENERIC_TEMPLATE_NAME = "E";

    public StandardCollectionSymbol(Type variableType, TokenSymbolInfo tokenSymbolInfo) {
        super(variableType, tokenSymbolInfo);
    }

    public StandardCollectionSymbol(String name, TokenSymbolInfo tokenSymbolInfo) {
        super(name, tokenSymbolInfo);
    }

    public StandardCollectionSymbol(String text, Type variableType, TokenSymbolInfo tokenSymbolInfo) {
        super(text, variableType, tokenSymbolInfo);
    }
}
