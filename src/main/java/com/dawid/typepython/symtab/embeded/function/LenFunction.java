package com.dawid.typepython.symtab.embeded.function;

import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.matching.NoMatchingFunctionException;
import com.dawid.typepython.symtab.symbol.Symbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.type.Type;
import type.CppVariableType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dawid on 12.09.2019 at 01:28.
 */
public class LenFunction extends EmbeddedFunction {
    public LenFunction() {
        super("len", CppVariableType.INT, new ArrayList<>());
    }

    @Override
    public String invoke(Symbol invoker, List<Symbol> parameters) {
        StringBuilder stringBuilder = new StringBuilder();

        if (parameters.size() > 1 || parameters.isEmpty() || !((TypedSymbol) parameters.get(0)).getVariableType().isCollection()) {
            throw new NoMatchingFunctionException();
        }

        stringBuilder.append(parameters.get(0).getDisplayText()).append(".size()");

        return stringBuilder.toString();
    }

    @Override
    public MatchType parametersMatch(List<Type> parameterTypes) {
        boolean noneMatch = parameterTypes.size() > 1 || parameterTypes.isEmpty() || ! parameterTypes.get(0).isCollection();

        if (noneMatch) {
            return MatchType.NONE;
        }

        return MatchType.FULL;
    }

    @Override
    public boolean parametersCountMatch(List<Type> parameters) {
        return parameters.size() == 1;
    }
}
