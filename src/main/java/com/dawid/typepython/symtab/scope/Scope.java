package com.dawid.typepython.symtab.scope;

import com.dawid.typepython.TokenSymbolInfo;
import com.dawid.typepython.symtab.matching.AmbiguousFunctionCallException;
import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.matching.MatchingResult;
import com.dawid.typepython.symtab.symbol.FunctionSymbol;
import com.dawid.typepython.symtab.symbol.ImportSymbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.symbol.VariableSymbol;
import com.dawid.typepython.symtab.type.SymbolType;
import com.dawid.typepython.symtab.type.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Scope implements Serializable {
    private String namespace;
    private Scope parentScope;
    private ScopeType scopeType;
    private List<TypedSymbol> variables = new ArrayList<>();
    private List<FunctionSymbol> functionSymbols = new ArrayList<>();
    private List<ImportScope> importScopes = new ArrayList<>();

    public Scope(ScopeType scopeType, List<TypedSymbol> symbols) {
        this.scopeType = scopeType;
        this.functionSymbols = new ArrayList<>();
        this.variables = symbols;
    }

    public Scope(ScopeType scopeType, String namespace) {
        this.scopeType = scopeType;
        this.namespace = namespace;
    }

    public Scope(ScopeType main) {
        scopeType = main;
        variables = new ArrayList<>();
        functionSymbols = new ArrayList<>();
    }

    public Optional<String> getNamespace() {
        return Optional.ofNullable(namespace);
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

    public Optional<TypedSymbol> findAtom(String name) {
        Optional<TypedSymbol> variable = variables.stream().filter(it -> it.getName().equals(name)).findFirst();

        if (!variable.isPresent()) {
            variable = Optional.of(findFunction(name)).filter(Optional::isPresent).map(Optional::get);
        }

        if (!variable.isPresent()) {
            variable = findImport(name).map(it -> new ImportSymbol(SymbolType.IMPORT, it));
        }

        if (!variable.isPresent()) {
            variable = getParentScope().flatMap(it -> it.findAtom(name));
        }

        return variable;
    }

    private Optional<ImportScope> findImport(String name) {
        return importScopes
                .stream()
                .filter(it -> it.getNamespace()
                        .filter(namespace -> namespace.equals(name))
                        .isPresent())
                .findFirst();
    }

    private Optional<FunctionSymbol> findFunction(String name) {
        return functionSymbols.stream().filter(it -> it.getName().equals(name)).findFirst();
    }

    protected Optional<Scope> getParentScope() {
        return Optional.ofNullable(parentScope);
    }

    public void setEnclosingScope(Scope enclosingScope) {
        parentScope = enclosingScope;
    }

    public void addVariable(TypedSymbol assignable) {
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

    public MatchingResult findFunction(String functionName, List<Type> parameterTypes, TokenSymbolInfo tokenSymbolInfo) {
        List<FunctionSymbol> functions = new ArrayList<>();
        findFunction(functionName, parameterTypes, functions);

        List<FunctionSymbol> partialMatchingFunction = new ArrayList<>();

        for (FunctionSymbol functionSymbol : functions) {
            MatchType matchType = functionSymbol.parametersMatch(parameterTypes);
            if (matchType == MatchType.FULL) {
                return new MatchingResult(functionSymbol, matchType);
            }

            if (matchType == MatchType.PARTIAL) {
                partialMatchingFunction.add(functionSymbol);
            }
        }

        if (partialMatchingFunction.size() > 1) {
            throw new AmbiguousFunctionCallException(functionName, parameterTypes, tokenSymbolInfo, partialMatchingFunction);
        }

        if (partialMatchingFunction.isEmpty()) {
            return new MatchingResult(null, MatchType.NONE);
        }

        return new MatchingResult(partialMatchingFunction.get(0), MatchType.PARTIAL);
    }

    private void findFunction(String text, List<Type> parameters, List<FunctionSymbol> functions) {
        functionSymbols.stream()
                .filter(it -> it.getName().equals(text))
                .filter(it -> it.parametersCountMatch(parameters))
                .forEach(functions::add);

        if (parentScope != null) {
            parentScope.findFunction(text, parameters, functions);
        }
    }

    public void addImportScope(ImportScope importScope) {
        this.importScopes.add(importScope);
    }
}
