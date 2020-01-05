package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.CompilerException;
import com.dawid.typepython.TokenSymbolInfo;
import com.dawid.typepython.symtab.type.Type;

/**
 * Created by Dawid on 27.07.2019 at 17:13.
 */
public class VariableTypeMissMatchException extends CompilerException {

    public VariableTypeMissMatchException(String message, TokenSymbolInfo tokenSymbolInfo) {
        super(message, tokenSymbolInfo);
    }

    public VariableTypeMissMatchException(TokenSymbolInfo tokenSymbolInfo, Type expected, Type actual) {
        super("Expected type: " + (expected == null ? "" : expected.getPythonType()) + " | actual: " + tokenSymbolInfo.getSourceText() + " -> type: " + (actual == null? "" : actual.getPythonType()), tokenSymbolInfo);
    }
}
