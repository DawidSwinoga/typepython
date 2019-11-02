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
        super("print", new FunctionType(CppVariableType.VOID), new ArrayList<>());
    }

    @Override
    public FunctionResult invoke(Symbol invoker, List<Symbol> parameters) {
        StringBuilder stringBuilder = new StringBuilder();

        if (CollectionUtils.isNotEmpty(parameters)) {
            stringBuilder.append("cout ");
        }

        for (Symbol symbol : parameters) {
            if (!((TypedSymbol) symbol).getVariableType().isCollection()) {
                stringBuilder.append(" << ").append(symbol.getDisplayText()).append(" ");
            } else {
                throw new IllegalFunctionParameter();
            }
        }
        stringBuilder.append(" << endl;");
        return new FunctionResult(stringBuilder.toString(), getVariableType());
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
