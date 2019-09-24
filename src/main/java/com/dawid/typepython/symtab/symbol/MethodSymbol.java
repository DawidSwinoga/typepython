package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.type.Type;

import java.util.List;
import java.util.stream.Collectors;

public class MethodSymbol extends FunctionSymbol {
    public MethodSymbol(String name, Type returnType, List<TypedSymbol> parameters) {
        super(name, returnType, parameters);
    }
}
