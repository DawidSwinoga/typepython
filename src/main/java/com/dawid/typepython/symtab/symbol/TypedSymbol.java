package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.matching.AmbiguousFunctionCallException;
import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.matching.MatchingResult;
import com.dawid.typepython.symtab.scope.Scope;
import com.dawid.typepython.symtab.type.SymbolType;
import com.dawid.typepython.symtab.type.Type;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Dawid on 07.07.2019 at 21:47.
 */

@Setter
public class TypedSymbol extends Symbol {
    @Getter
    protected Type variableType;
    @Getter
    private boolean collectionElement = false;

    public TypedSymbol(Type variableType) {
        this.variableType = variableType;
    }

    public TypedSymbol(String name) {
        super(name);
    }

    public TypedSymbol(String name, Type variableType) {
        super(name);
        this.variableType = variableType;
    }

    public TypedSymbol(SymbolType symbolType, String text) {
        super(symbolType, text);
    }

    public TypedSymbol(SymbolType symbolType, Scope scope) {
        super(symbolType, scope);
    }

    public String getCppNameType() {
        return variableType.getCppNameType();
    }

    @Override
    public boolean isDeclaredInScope() {
        return collectionElement || super.isDeclaredInScope();
    }

    public MatchType match(TypedSymbol t) {
        return variableType.match(t.getVariableType());
    }

    public boolean isCollection() {
        return variableType.isCollection();
    }

    public MatchingResult findMethod(String methodName, List<Type> parameters) {
        List<MethodSymbol> methods = variableType.getMethodSymbol()
                .stream()
                .filter(it -> it.getName().equals(methodName))
                .collect(Collectors.toList());

        List<FunctionSymbol> partialMatchingMethods = new ArrayList<>();

        for (MethodSymbol methodSymbol : methods) {
            MatchType matchType = methodSymbol.parametersMatch(parameters);
            if (matchType == MatchType.FULL) {
                return new MatchingResult(methodSymbol, matchType);
            }

            if (matchType == MatchType.PARTIAL) {
                partialMatchingMethods.add(methodSymbol);
            }
        }

        if (partialMatchingMethods.size() > 1) {
            throw new AmbiguousFunctionCallException(partialMatchingMethods);
        }

        if (partialMatchingMethods.isEmpty()) {
            return new MatchingResult(null, MatchType.NONE);
        }

        return new MatchingResult(partialMatchingMethods.get(0), MatchType.PARTIAL);
    }
}
