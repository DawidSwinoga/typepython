package com.dawid.typepython.symtab.type;

import com.dawid.typepython.CompilerException;
import com.dawid.typepython.TokenSymbolInfo;

/**
 * Created by Dawid on 07.07.2019 at 21:40.
 */
public class UnsupportedGenericTypeException extends CompilerException {
    public UnsupportedGenericTypeException(String pythonName, TokenSymbolInfo tokenSymbolInfo) {
        super("Unsupported generic type: " + pythonName + ".",tokenSymbolInfo);
    }
}
