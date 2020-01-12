package com.dawid.typepython.symtab.type.collection;

import com.dawid.typepython.CompilerException;
import com.dawid.typepython.TokenSymbolInfo;

/**
 * Created by Dawid on 21.07.2019 at 01:44.
 */
public class TypesMissMatchException extends CompilerException {
    public TypesMissMatchException(TokenSymbolInfo tokenSymbolInfo) {
        super(tokenSymbolInfo);
    }

    public TypesMissMatchException(String message, TokenSymbolInfo tokenSymbolInfo) {
        super(message, tokenSymbolInfo);
    }

    public TypesMissMatchException(String message, Throwable cause, TokenSymbolInfo tokenSymbolInfo) {
        super(message, cause, tokenSymbolInfo);
    }

    public TypesMissMatchException(Throwable cause, TokenSymbolInfo tokenSymbolInfo) {
        super(cause, tokenSymbolInfo);
    }
}
