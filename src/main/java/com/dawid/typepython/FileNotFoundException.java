package com.dawid.typepython;

/**
 * Created by Dawid on 12.10.2019 at 17:38.
 */
public class FileNotFoundException extends CompilerException {
    public FileNotFoundException(String message, TokenSymbolInfo tokenSymbolInfo) {
        super("Cannot find file " + message, tokenSymbolInfo);
    }
}
