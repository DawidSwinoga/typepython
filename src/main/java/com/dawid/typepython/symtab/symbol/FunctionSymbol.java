package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.TokenSymbolInfo;
import com.dawid.typepython.symtab.FunctionResult;
import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.type.FunctionType;
import com.dawid.typepython.symtab.type.Type;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionSymbol extends VariableSymbol {
    @Getter
    private final List<TypedSymbol> parameters;

    public FunctionSymbol(String name, TokenSymbolInfo tokenSymbolInfo) {
        super(name, tokenSymbolInfo);
        parameters = new ArrayList<>();
    }

    public FunctionSymbol(String name, Type returnType, List<TypedSymbol> parameters, TokenSymbolInfo tokenSymbolInfo) {
        super(name, tokenSymbolInfo);
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

    @Override
    public boolean isAssignable() {
        return false;
    }

    public FunctionResult invoke(Symbol invoker, List<TypedSymbol> parameters) {
        String text = getDisplayText() + "(" + parameters.stream().map(Symbol::getDisplayText).collect(Collectors.joining(", ")) + ")";
        return new FunctionResult(text, getVariableType() instanceof FunctionType ? ((FunctionType)getVariableType()).getReturnType() : getVariableType());
    }

    public boolean parametersCountMatch(List<Type> parameters) {
        return getParametersCount() == parameters.size();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName())
                .append(" (")
                .append(parameters.stream().map(TypedSymbol::getPythonNameType).collect(Collectors.joining(",")))
                .append(")");
        if (StringUtils.isNotBlank(getPythonNameType())) {
            builder
                    .append(": ")
                    .append(getPythonNameType());
        }
        return builder.toString();
    }
}
