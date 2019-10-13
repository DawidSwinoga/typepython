package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.FunctionResult;
import com.dawid.typepython.symtab.type.FunctionType;

import java.util.List;
import java.util.stream.Collectors;

public class MethodSymbol extends FunctionSymbol {
    public MethodSymbol(String name, FunctionType returnType, List<TypedSymbol> parameters) {
        super(name, returnType, parameters);
    }

    @Override
    public FunctionResult invoke(Symbol invoker, List<Symbol> parameters) {
        String text = getDisplayText() + "(" + parameters.stream().map(Symbol::getDisplayText).collect(Collectors.joining(",")) + ")";
        return new FunctionResult(text, getVariableType());
    }
}
