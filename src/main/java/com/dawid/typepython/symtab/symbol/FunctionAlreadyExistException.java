package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.CompilerException;
import com.dawid.typepython.TokenSymbolInfo;

/**
 * Created by Dawid on 11.09.2019 at 12:31.
 */
public class FunctionAlreadyExistException extends CompilerException {
    public FunctionAlreadyExistException(String message, TokenSymbolInfo tokenSymbolInfo) {
        super(message, tokenSymbolInfo);
    }
}
