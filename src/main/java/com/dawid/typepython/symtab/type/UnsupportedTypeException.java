package com.dawid.typepython.symtab.type;

import com.dawid.typepython.CompilerException;
import com.dawid.typepython.TokenSymbolInfo;

/**
 * Created by Dawid on 04.01.2020 at 23:21.
 */
public class UnsupportedTypeException extends CompilerException {
    public UnsupportedTypeException(String pythonName, TokenSymbolInfo tokenSymbolInfo) {
        super("Unsupported type: " + pythonName + " ",tokenSymbolInfo);
    }
}
