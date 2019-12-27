package com.dawid.typepython.symtab.scope;

import lombok.Getter;

@Getter
public class GlobalScope extends Scope {
    public GlobalScope(String scopeFileName) {
        super(ScopeType.MAIN, null, scopeFileName);
    }

    public GlobalScope(ScopeType scopeType, String namespace, String scopeFileName) {
        super(scopeType, namespace, scopeFileName);
    }
}
