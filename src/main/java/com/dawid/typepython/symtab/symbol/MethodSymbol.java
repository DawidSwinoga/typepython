package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.type.Type;

import java.util.List;

public class MethodSymbol extends FunctionSymbol {
    public MethodSymbol(String name, Type returnType, List<TypedSymbol> parameters) {
        super(name, returnType, parameters);
    }
}
