package com.dawid.typepython.symtab.type;

import com.dawid.typepython.CompilerException;
import com.dawid.typepython.TokenSymbolInfo;

/**
 * Created by Dawid on 15.10.2019 at 20:36.
 */
public class TypeNotDefinedException extends CompilerException {
    public TypeNotDefinedException(String displayText, TokenSymbolInfo tokenSymbolInfo) {
        super("Type not defined for: " + displayText + ".", tokenSymbolInfo);
    }
}
