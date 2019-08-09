package com.dawid.typepython.symtab;

import com.dawid.typepython.symtab.symbol.FunctionSymbol;
import com.dawid.typepython.symtab.symbol.VariableSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Scope {
    private Scope parentScope;
    private ScopeType scopeType;
    private List<VariableSymbol> variables;
    private List<FunctionSymbol> functionSymbols;

    public Scope(ScopeType scopeType, List<VariableSymbol> symbols) {
        this.scopeType = scopeType;
        this.functionSymbols = new ArrayList<>();
        this.variables = symbols;
    }

    public Scope(ScopeType main) {
        scopeType = main;
        variables = new ArrayList<>();
        functionSymbols = new ArrayList<>();
    }

    public boolean isLocalScope() {
        return scopeType == ScopeType.LOCAL;
    }

    public boolean isFunction() {
        return false;
    }

    public void addFunctionSymbol(FunctionSymbol functionSymbol) {
        functionSymbols.add(functionSymbol);
    }

    public Optional<VariableSymbol> findAtom(String name) {
        Optional<VariableSymbol> variable = variables.stream().filter(it -> it.getText().equals(name)).findFirst();

        if (!variable.isPresent()) {
            variable = Optional.of(findFunction(name)).filter(Optional::isPresent).map(Optional::get);
        }

        if (!variable.isPresent()) {
            variable = getParentScope().flatMap(it -> it.findAtom(name));
        }

        return variable;
    }

    private Optional<FunctionSymbol> findFunction(String name) {
        return functionSymbols.stream().filter(it -> it.getText().equals(name)).findFirst();
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

    public void addAllVariables(List<VariableSymbol> symbols) {
        variables.addAll(symbols);
    }

    public Scope getEnclosingScope() {
        return parentScope;
    }

    public FunctionScope getFunctionScope() {
        if (isFunction()) {
            return (FunctionScope)this;
        }

        if (parentScope != null) {
            return parentScope.getFunctionScope();
        }

        return null;
    }
}
