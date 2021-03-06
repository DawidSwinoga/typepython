package com.dawid.typepython.symtab.embeded.function;

import com.dawid.typepython.symtab.FunctionResult;
import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.matching.NoMatchingFunctionException;
import com.dawid.typepython.symtab.symbol.Symbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.type.CppVariableType;
import com.dawid.typepython.symtab.type.FunctionType;
import com.dawid.typepython.symtab.type.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Dawid on 08.12.2019 at 14:09.
 */
public class ToStringFunction extends EmbeddedFunction {
    public ToStringFunction() {
        super("toString", new FunctionType(CppVariableType.STRING), new ArrayList<>());
        setDisplayText("std::to_string");
    }

    @Override
    public FunctionResult invoke(Symbol invoker, List<TypedSymbol> parameters) {
        StringBuilder stringBuilder = new StringBuilder();

        if (parameters.size() != 1 || parameters.get(0).getVariableType().isCollection()) {
            throw new NoMatchingFunctionException("toString", parameters.stream().map(TypedSymbol::getVariableType).collect(Collectors.toList()), invoker.getTokenSymbolInfo());
        }

        stringBuilder.append(getDisplayText()).append("(").append(parameters.get(0).getDisplayText()).append(")");

        return new FunctionResult(stringBuilder.toString(), getVariableType());
    }

    @Override
    public MatchType parametersMatch(List<Type> parameterTypes) {
        boolean match = parameterTypes.size() == 1 && !parameterTypes.get(0).isCollection();

        if (match) {
            return MatchType.FULL;
        }

        return MatchType.NONE;
    }

    @Override
    public boolean parametersCountMatch(List<Type> parameters) {
        return parameters.size() == 1;
    }
}
