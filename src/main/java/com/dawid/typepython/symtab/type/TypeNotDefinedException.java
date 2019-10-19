package com.dawid.typepython.symtab.type;

/**
 * Created by Dawid on 15.10.2019 at 20:36.
 */
public class TypeNotDefinedException extends RuntimeException {
    public TypeNotDefinedException(String message) {
        super(message);
    }
}
