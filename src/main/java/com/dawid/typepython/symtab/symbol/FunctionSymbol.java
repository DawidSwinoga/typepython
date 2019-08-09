package com.dawid.typepython.symtab.symbol;

import java.util.List;

public class FunctionSymbol extends VariableSymbol {
    private final List<VariableSymbol> parameters;
    private final TypedSymbol returnType;

    public FunctionSymbol(String name, TypedSymbol returnType, List<VariableSymbol> parameters) {
        super(name);
        this.parameters = parameters;
        this.returnType = returnType;
    }
}
