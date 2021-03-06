package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.TokenSymbolInfo;
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

    public ClassSymbol(Type variableType, TokenSymbolInfo tokenSymbolInfo) {
        super(variableType, tokenSymbolInfo);
    }

    public ClassSymbol(String name, TokenSymbolInfo tokenSymbolInfo) {
        super(name, tokenSymbolInfo);
    }

    public ClassSymbol(String name, Type variableType, TokenSymbolInfo tokenSymbolInfo) {
        super(name, variableType, tokenSymbolInfo);
    }

    public ClassSymbol(SymbolType symbolType, String text, TokenSymbolInfo tokenSymbolInfo) {
        super(symbolType, text, tokenSymbolInfo);
    }

    public void addMethod(MethodSymbol methodSymbol) {
        methods.add(methodSymbol);
    }
}
