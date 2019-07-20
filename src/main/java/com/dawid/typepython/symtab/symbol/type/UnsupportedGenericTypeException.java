package com.dawid.typepython.symtab.symbol.type;

/**
 * Created by Dawid on 07.07.2019 at 21:40.
 */
public class UnsupportedGenericTypeException extends RuntimeException {
    public UnsupportedGenericTypeException(String message) {
        super(message);
    }
}
