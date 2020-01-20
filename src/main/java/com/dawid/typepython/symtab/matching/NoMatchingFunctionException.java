package com.dawid.typepython.symtab.matching;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.dawid.typepython.CompilerException;
import com.dawid.typepython.TokenSymbolInfo;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.type.Type;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Dawid on 25.08.2019 at 02:40.
 */
public class NoMatchingFunctionException extends CompilerException {
    private final String name;
    private final List<Type> parameters;

    public NoMatchingFunctionException(String name, List<Type> parameters, TokenSymbolInfo tokenSymbolInfo) {
        super(tokenSymbolInfo);
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public String getMessage() {
        String parametersType = Optional.ofNullable(parameters).map(params -> parameters.stream().map(Type::getPythonType).collect(Collectors.joining(","))).orElse("");
        return "Function " + getFunctionAlias(parametersType) + "not found.";
    }

    private String getFunctionAlias(String parametersType) {
        if (name != null) {
            return name + " (" + parametersType + ") ";
        } else {
            return StringUtils.EMPTY;
        }
    }
}
