package com.dawid.typepython.cpp.code.operator;

/**
 * Created by Dawid on 22.06.2019 at 15:57.
 */

public class UnsupportedCompareOperationException extends RuntimeException {
    public UnsupportedCompareOperationException() {
    }

    public UnsupportedCompareOperationException(String message) {
        super(message);
    }

    public UnsupportedCompareOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedCompareOperationException(Throwable cause) {
        super(cause);
    }

    public UnsupportedCompareOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
