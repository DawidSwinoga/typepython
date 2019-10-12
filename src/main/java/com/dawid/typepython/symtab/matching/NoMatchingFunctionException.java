package com.dawid.typepython.symtab.matching;

/**
 * Created by Dawid on 25.08.2019 at 02:40.
 */
public class NoMatchingFunctionException extends RuntimeException {
    public NoMatchingFunctionException() {
    }

    public NoMatchingFunctionException(String name) {
        super(name);
    }
}
