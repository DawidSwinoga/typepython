package com.dawid.typepython.symtab;

import com.dawid.typepython.symtab.symbol.Symbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.symbol.VariableSymbol;

import java.util.ArrayList;
import java.util.List;

public class FunctionScope extends Scope {
    private List<VariableSymbol> parameters;
    private final TypedSymbol returnType;

    public FunctionScope(ScopeType scopeType, List<VariableSymbol> parameters, TypedSymbol returnType) {
        super(scopeType);
        this.parameters = parameters;
        this.returnType = returnType;
    }

    public void addParameters(List<VariableSymbol> parameters) {
        this.parameters.addAll(parameters);
    }
}
