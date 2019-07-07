package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.symbol.type.SymbolType;

/**
 * Created by Dawid on 07.07.2019 at 15:26.
 */
public class LogicalSymbol extends Symbol {
    public LogicalSymbol(String name) {
        super(name);
    }

    @Override
    public SymbolType getSymbolType() {
        return SymbolType.LOGICAL_OPERATOR;
    }

//    public static LogicalSymbol of(String text) {
//        return new LogicalSymbol()
//    }
}
