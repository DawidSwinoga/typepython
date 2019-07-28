package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.symbol.type.VariableType;

import java.util.ArrayList;
import java.util.List;

public class FunctionSymbol extends TypedSymbol {
    private final List<TypedSymbol> parameters = new ArrayList<>();

    public FunctionSymbol(VariableType variableType) {
        super(variableType);
    }

    public FunctionSymbol(String name) {
        super(name);
    }

    public FunctionSymbol(String name, VariableType variableType) {
        super(name, variableType);
    }
}
