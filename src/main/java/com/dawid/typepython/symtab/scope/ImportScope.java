package com.dawid.typepython.symtab.scope;

import lombok.Getter;

@Getter
public class ImportScope extends GlobalScope {
    private String scopeIdentifier;


    public ImportScope(String scopeFileName, String namespace, String scopeIdentifier) {
        super(ScopeType.IMPORT, namespace, scopeFileName);
        this.scopeIdentifier = scopeIdentifier;
    }
}
