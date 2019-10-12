package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.type.Type;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionSymbol extends VariableSymbol {
    @Getter
    private final List<TypedSymbol> parameters;

    public FunctionSymbol(String name, Type returnType, List<TypedSymbol> parameters) {
        super(name);
        this.parameters = parameters;
        this.variableType = returnType;
    }

    public List<Type> getParameterTypes() {
        return parameters.stream().map(TypedSymbol::getVariableType).collect(Collectors.toList());
    }

    public int getParametersCount() {
        return parameters.size();
    }

    public MatchType parametersMatch(List<Type> parameterTypes) {
        if (parameters.size() != parameterTypes.size()) {
            return MatchType.NONE;
        }

        List<Type> argumentTypes = parameters.stream().map(TypedSymbol::getVariableType).collect(Collectors.toList());
        MatchType matchType = MatchType.FULL;
        for (int i = 0; i < argumentTypes.size(); i++) {
            MatchType match = argumentTypes.get(i).match(parameterTypes.get(i));

            if (match == MatchType.NONE) {
                return match;
            }

            if (match == MatchType.PARTIAL) {
                matchType = match;
            }
        }

        return matchType;
    }

    public String invoke(Symbol invoker, List<Symbol> parameters) {
        return "";
    }

    public boolean parametersCountMatch(List<Type> parameters) {
        return getParametersCount() == parameters.size();
    }
}
