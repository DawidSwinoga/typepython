package com.dawid.typepython.symtab;

public class GlobalScope extends Scope {
    public GlobalScope() {
        super(ScopeType.MAIN);
    }
}
