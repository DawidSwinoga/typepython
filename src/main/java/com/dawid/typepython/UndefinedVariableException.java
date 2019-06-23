package com.dawid.typepython;

public class UndefinedVariableException extends RuntimeException {
    public UndefinedVariableException() {
        super();
    }

    public UndefinedVariableException(String s) {
        super(s);
    }

    public UndefinedVariableException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public UndefinedVariableException(Throwable throwable) {
        super(throwable);
    }

    protected UndefinedVariableException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
