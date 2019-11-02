package com.dawid.typepython.symtab.embeded.vector;

import com.dawid.typepython.TokenSymbolInfo;
import com.dawid.typepython.symtab.symbol.CollectionClassSymbol;
import com.dawid.typepython.symtab.type.Type;

/**
 * Created by Dawid on 15.10.2019 at 20:39.
 */
public class TupleSymbol extends CollectionClassSymbol {
    public static final String GENERIC_TEMPLATE_NAME = "E";

    public TupleSymbol(Type variableType, TokenSymbolInfo tokenSymbolInfo) {
        super(variableType, tokenSymbolInfo);
    }

    public TupleSymbol(String name, TokenSymbolInfo tokenSymbolInfo) {
        super(name, tokenSymbolInfo);
    }

    public TupleSymbol(String text, Type VariableType, TokenSymbolInfo tokenSymbolInfo) {
        super(text, VariableType, tokenSymbolInfo);
    }
}
