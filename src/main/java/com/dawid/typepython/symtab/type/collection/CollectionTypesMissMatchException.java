package com.dawid.typepython.symtab.type.collection;

import com.dawid.typepython.CompilerException;
import com.dawid.typepython.TokenSymbolInfo;

/**
 * Created by Dawid on 21.07.2019 at 01:44.
 */
public class CollectionTypesMissMatchException extends CompilerException {
    public CollectionTypesMissMatchException(TokenSymbolInfo tokenSymbolInfo) {
        super(tokenSymbolInfo);
    }

    public CollectionTypesMissMatchException(String message, TokenSymbolInfo tokenSymbolInfo) {
        super(message, tokenSymbolInfo);
    }

    public CollectionTypesMissMatchException(String message, Throwable cause, TokenSymbolInfo tokenSymbolInfo) {
        super(message, cause, tokenSymbolInfo);
    }

    public CollectionTypesMissMatchException(Throwable cause, TokenSymbolInfo tokenSymbolInfo) {
        super(cause, tokenSymbolInfo);
    }
}
