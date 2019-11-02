package com.dawid.typepython.symtab.matching;

import com.dawid.typepython.CompilerException;
import com.dawid.typepython.TokenSymbolInfo;
import com.dawid.typepython.symtab.symbol.FunctionSymbol;
import com.dawid.typepython.symtab.type.Type;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Dawid on 25.08.2019 at 02:38.
 */
public class AmbiguousFunctionCallException extends CompilerException {

    private final String functionName;
    private final List<Type> parameterTypes;
    private final List<FunctionSymbol> partialMatchingFunction;

    public AmbiguousFunctionCallException(String functionName, List<Type> parameterTypes, TokenSymbolInfo tokenSymbolInfo, List<FunctionSymbol> partialMatchingFunction) {
        super(tokenSymbolInfo);
        this.functionName = functionName;
        this.parameterTypes = parameterTypes;
        this.partialMatchingFunction = partialMatchingFunction;
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder();
        message.append("Ambiguous function call -> ")
                .append(functionName)
                .append("(")
                .append(parameterTypes.stream().map(Type::getPythonType).collect(Collectors.joining(",")))
                .append(")")
                .append("\n")
                .append("Candidates: \n");

        String candidates = partialMatchingFunction.stream().map(Objects::toString).collect(Collectors.joining("\n"));
        message.append(candidates);
        return message.toString();
    }
}
