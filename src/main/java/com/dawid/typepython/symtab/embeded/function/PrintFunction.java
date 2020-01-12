package com.dawid.typepython.symtab.embeded.function;

import com.dawid.typepython.symtab.FunctionResult;
import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.symbol.Symbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.type.CppVariableType;
import com.dawid.typepython.symtab.type.FunctionType;
import com.dawid.typepython.symtab.type.Type;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dawid on 11.09.2019 at 23:19.
 */
public class PrintFunction extends EmbeddedFunction {
    public PrintFunction() {
        this("print");
    }

    public PrintFunction(String name) {
        super(name, new FunctionType(CppVariableType.VOID), new ArrayList<>());
    }

    @Override
    public FunctionResult invoke(Symbol invoker, List<TypedSymbol> parameters) {
        StringBuilder stringBuilder = print(parameters);
        return new FunctionResult(stringBuilder.toString(), getVariableType());
    }

    protected StringBuilder print(List<TypedSymbol> parameters) {
        StringBuilder stringBuilder = new StringBuilder();

        if (CollectionUtils.isNotEmpty(parameters)) {
            stringBuilder.append("std::cout ");
        }

        for (Symbol symbol : parameters) {
            if (!((TypedSymbol) symbol).getVariableType().isCollection()) {
                stringBuilder.append(" << ").append(symbol.getDisplayText()).append(" ").append(" << \" \" ");
            } else {
                throw new IllegalFunctionParameter();
            }
        }
        return stringBuilder;
    }

    @Override
    public MatchType parametersMatch(List<Type> parameterTypes) {
        boolean basicType = parameterTypes.stream().noneMatch(Type::isCollection);
        if (basicType) {
            return MatchType.FULL;
        }

        return MatchType.NONE;
    }

    @Override
    public boolean parametersCountMatch(List<Type> parameters) {
        return true;
    }
}
