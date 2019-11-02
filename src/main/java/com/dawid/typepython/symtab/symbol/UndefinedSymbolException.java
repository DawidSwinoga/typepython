package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.CompilerException;
import com.dawid.typepython.TokenSymbolInfo;

public class UndefinedSymbolException extends CompilerException {
    public UndefinedSymbolException(String symbol, TokenSymbolInfo tokenSymbolInfo) {
        super("Undefined symbol: " + symbol, tokenSymbolInfo);
    }
}
