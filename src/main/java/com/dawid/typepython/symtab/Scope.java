package com.dawid.typepython.symtab;

import com.dawid.typepython.symtab.symbol.VariableSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Scope {
    private Scope parentScope;
    private ScopeType scopeType;
    private List<VariableSymbol> variables;

    public Scope(ScopeType scopeType) {
        this.scopeType = scopeType;
        this.variables = new ArrayList<>();
    }

    public boolean isLocalScope() {
        return scopeType == ScopeType.LOCAL;
    }

    public Optional<VariableSymbol> findVariable(String name) {
        Optional<VariableSymbol> variable = variables.stream().filter(it -> it.getText().equals(name)).findFirst();

        if (!variable.isPresent()) {
            variable = getParentScope().flatMap(it -> it.findVariable(name));
        }

        return variable;
    }

    protected Optional<Scope> getParentScope() {
        return Optional.ofNullable(parentScope);
    }

    public void setEnclosingScope(Scope enclosingScope) {
        parentScope = enclosingScope;
    }

    public void addVariable(VariableSymbol assignable) {
        variables.add(assignable);
        assignable.setScope(this);
    }

    public Scope getEnclosingScope() {
        return parentScope;
    }
}
