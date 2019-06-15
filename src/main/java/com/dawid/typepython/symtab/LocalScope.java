package com.dawid.typepython.symtab;

public class LocalScope extends Scope {
    public LocalScope() {
        super(ScopeType.LOCAL);
    }

    @Override
    public boolean isLocalScope() {
        return true;
    }
}
