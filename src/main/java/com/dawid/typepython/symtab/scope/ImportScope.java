package com.dawid.typepython.symtab.scope;

public class ImportScope extends GlobalScope {
    private String scopeFileName;

    public ImportScope(String scopeFileName) {
        super(ScopeType.IMPORT);
        this.scopeFileName = scopeFileName;
    }
}
