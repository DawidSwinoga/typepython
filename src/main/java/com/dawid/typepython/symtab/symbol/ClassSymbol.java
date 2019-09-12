package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.type.SymbolType;
import com.dawid.typepython.symtab.type.VariableType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dawid on 11.09.2019 at 20:52.
 */
@Getter
public class ClassSymbol extends VariableSymbol {
    private List<FunctionSymbol> methods = new ArrayList<>();

    public ClassSymbol(VariableType variableType) {
        super(variableType);
    }

    public ClassSymbol(String name) {
        super(name);
    }

    public ClassSymbol(String name, VariableType variableType) {
        super(name, variableType);
    }

    public ClassSymbol(SymbolType symbolType, String text) {
        super(symbolType, text);
    }
}
