package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.TokenSymbolInfo;
import com.dawid.typepython.symtab.FunctionResult;
import com.dawid.typepython.symtab.type.Type;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public class MethodSymbol extends FunctionSymbol {
    @Getter
    protected boolean returnTypeAssignable;

    public MethodSymbol(String name, Type returnType, List<TypedSymbol> parameters, boolean returnTypeAssignable, TokenSymbolInfo tokenSymbolInfo) {
        super(name, returnType, parameters, tokenSymbolInfo);
        this.returnTypeAssignable = returnTypeAssignable;
    }

    public MethodSymbol(String name, Type returnType, List<TypedSymbol> parameters, TokenSymbolInfo tokenSymbolInfo) {
        super(name, returnType, parameters, tokenSymbolInfo);
        this.returnTypeAssignable = true;
    }

    @Override
    public FunctionResult invoke(Symbol invoker, List<TypedSymbol> parameters) {
        String text = getDisplayText() + "(" + parameters.stream().map(Symbol::getDisplayText).collect(Collectors.joining(",")) + ")";
        return new FunctionResult(text, getVariableType(), returnTypeAssignable);
    }
}
