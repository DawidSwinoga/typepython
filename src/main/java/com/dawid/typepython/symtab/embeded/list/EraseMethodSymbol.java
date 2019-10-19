package com.dawid.typepython.symtab.embeded.list;

import com.dawid.typepython.symtab.FunctionResult;
import com.dawid.typepython.symtab.symbol.MethodSymbol;
import com.dawid.typepython.symtab.symbol.Symbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.type.FunctionType;

import java.util.List;
import java.util.stream.Collectors;

public class EraseMethodSymbol extends MethodSymbol {
    public EraseMethodSymbol(String name, FunctionType returnType, List<TypedSymbol> parameters) {
        super(name, returnType, parameters, false);
    }

    @Override
    public FunctionResult invoke(Symbol invoker, List<Symbol> parameters) {
        String displayText = getDisplayText() + "(" + invoker.getDisplayText() + ".begin() + " + parameters.stream().map(Symbol::getDisplayText).collect(Collectors.joining(",")) + ")";
        return new FunctionResult(displayText, getVariableType());
    }
}
