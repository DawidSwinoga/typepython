package com.dawid.typepython.symtab.scope;

public class GlobalScope extends Scope {
    public GlobalScope() {
        super(ScopeType.MAIN);
    }
}
