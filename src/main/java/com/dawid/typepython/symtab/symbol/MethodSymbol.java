package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.FunctionResult;
import com.dawid.typepython.symtab.type.Type;

import java.util.List;
import java.util.stream.Collectors;

public class MethodSymbol extends FunctionSymbol {
    protected boolean returnTypeAssignable;

    public MethodSymbol(String name, Type returnType, List<TypedSymbol> parameters,  boolean returnTypeAssignable) {
        super(name, returnType, parameters);
        this.returnTypeAssignable = returnTypeAssignable;
    }

    public MethodSymbol(String name, Type returnType, List<TypedSymbol> parameters) {
        super(name, returnType, parameters);
        this.returnTypeAssignable = true;
    }

    @Override
    public FunctionResult invoke(Symbol invoker, List<Symbol> parameters) {
        String text = getDisplayText() + "(" + parameters.stream().map(Symbol::getDisplayText).collect(Collectors.joining(",")) + ")";
        return new FunctionResult(text, getVariableType(), returnTypeAssignable);
    }
}
