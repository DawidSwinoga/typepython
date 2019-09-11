package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.symbol.matching.MatchType;
import com.dawid.typepython.symtab.symbol.type.VariableType;
import lombok.Getter;

import java.util.List;

public class FunctionSymbol extends VariableSymbol {
    @Getter
    private final List<VariableSymbol> parameters;

    public FunctionSymbol(String name, VariableType returnType, List<VariableSymbol> parameters) {
        super(name);
        this.parameters = parameters;
        this.variableType = returnType;
    }

    public int getParametersCount() {
        return parameters.size();
    }

    public MatchType parametersMatch(List<Symbol> symbols) {
        if (parameters.size() != symbols.size()) {
            return MatchType.NONE;
        }

        MatchType matchType = MatchType.FULL;
        for (int i = 0; i < parameters.size(); i++) {
            MatchType match = parameters.get(i).match((TypedSymbol) symbols.get(i));

            if (match == MatchType.NONE) {
                return match;
            }

            if (match == MatchType.PARTIAL) {
                matchType = match;
            }
        }

        return matchType;
    }
}
