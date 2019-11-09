package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.TokenSymbolInfo;
import com.dawid.typepython.symtab.type.Type;

/**
 * Created by Dawid on 20.07.2019 at 15:55.
 */
public class CollectionClassSymbol extends ClassSymbol {

    public CollectionClassSymbol(Type variableType, TokenSymbolInfo tokenSymbolInfo) {
        super(variableType, tokenSymbolInfo);
    }

    public CollectionClassSymbol(String name, TokenSymbolInfo tokenSymbolInfo) {
        super(name, tokenSymbolInfo);
    }

    public CollectionClassSymbol(String text, Type VariableType, TokenSymbolInfo tokenSymbolInfo) {
        super(text, VariableType, tokenSymbolInfo);
    }
}
