package com.dawid.typepython.symtab.scope;

public class ImportScope extends GlobalScope {
    private String scopeFileName;

    public ImportScope(String scopeFileName, String namespace) {
        super(ScopeType.IMPORT, namespace);
        this.scopeFileName = scopeFileName;
    }
}
