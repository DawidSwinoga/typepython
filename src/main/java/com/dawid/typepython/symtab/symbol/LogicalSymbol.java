package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.TokenSymbolInfo;
import com.dawid.typepython.symtab.type.SymbolType;

/**
 * Created by Dawid on 07.07.2019 at 15:26.
 */
public class LogicalSymbol extends Symbol {
    public LogicalSymbol(String name, TokenSymbolInfo tokenSymbolInfo) {
        super(name, tokenSymbolInfo);
    }

    @Override
    public SymbolType getSymbolType() {
        return SymbolType.LOGICAL_OPERATOR;
    }
}
