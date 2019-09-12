package com.dawid.typepython.symtab.type.collection;

/**
 * Created by Dawid on 21.07.2019 at 01:44.
 */
public class CollectionTypesMissmatchException extends RuntimeException {
    public CollectionTypesMissmatchException() {
    }

    public CollectionTypesMissmatchException(String message) {
        super(message);
    }

    public CollectionTypesMissmatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public CollectionTypesMissmatchException(Throwable cause) {
        super(cause);
    }
}
