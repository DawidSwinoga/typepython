package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.type.SymbolType;
import com.dawid.typepython.symtab.type.Type;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dawid on 11.09.2019 at 20:52.
 */
@Getter
public class ClassSymbol extends VariableSymbol {
    private final List<FunctionSymbol> methods = new ArrayList<>();

    public ClassSymbol(Type variableType) {
        super(variableType);
    }

    public ClassSymbol(String name) {
        super(name);
    }

    public ClassSymbol(String name, Type variableType) {
        super(name, variableType);
    }

    public ClassSymbol(SymbolType symbolType, String text) {
        super(symbolType, text);
    }
}
