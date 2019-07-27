package com.dawid.typepython.symtab.symbol;

/**
 * Created by Dawid on 27.07.2019 at 17:13.
 */
public class VariableTypeMissmatchException extends RuntimeException {
    public VariableTypeMissmatchException() {
    }

    public VariableTypeMissmatchException(String message) {
        super(message);
    }

    public VariableTypeMissmatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
