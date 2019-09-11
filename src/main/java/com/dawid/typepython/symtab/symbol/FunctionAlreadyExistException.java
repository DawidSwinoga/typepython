package com.dawid.typepython.symtab.symbol;

/**
 * Created by Dawid on 11.09.2019 at 12:31.
 */
public class FunctionAlreadyExistException extends RuntimeException {
    public FunctionAlreadyExistException() {
    }

    public FunctionAlreadyExistException(String message) {
        super(message);
    }

    public FunctionAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public FunctionAlreadyExistException(Throwable cause) {
        super(cause);
    }
}
