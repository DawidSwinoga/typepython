package com.dawid.typepython.symtab.embeded.list;

import java.util.List;
import java.util.stream.Collectors;

import com.dawid.typepython.symtab.symbol.MethodSymbol;
import com.dawid.typepython.symtab.symbol.Symbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.type.Type;

public class EraseMethodSymbol extends MethodSymbol {
    public EraseMethodSymbol(String name, Type returnType, List<TypedSymbol> parameters) {
        super(name, returnType, parameters);
    }

    @Override
    public String invoke(Symbol invoker, List<Symbol> parameters) {
        return getDisplayText() + "(" + invoker.getDisplayText() + ".begin() + " + parameters.stream().map(Symbol::getDisplayText).collect(Collectors.joining(",")) + ")";
    }
}
