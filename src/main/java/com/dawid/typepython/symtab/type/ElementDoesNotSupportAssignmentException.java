package com.dawid.typepython.symtab.type;

/**
 * Created by Dawid on 15.10.2019 at 21:00.
 */
public class ElementDoesNotSupportAssignmentException extends RuntimeException {
    public ElementDoesNotSupportAssignmentException(String message) {
        super(message);
    }
}
