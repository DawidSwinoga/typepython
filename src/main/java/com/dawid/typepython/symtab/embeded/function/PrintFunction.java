package com.dawid.typepython.symtab.embeded.function;

import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.symbol.Symbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.type.Type;
import type.CppVariableType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dawid on 11.09.2019 at 23:19.
 */
public class PrintFunction extends EmbeddedFunction {
    public PrintFunction() {
        super("print", CppVariableType.VOID, new ArrayList<>());
    }

    @Override
    public String invoke(Symbol invoker, List<Symbol> parameters) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Symbol symbol : parameters) {
            if (!((TypedSymbol) symbol).getVariableType().isCollection()) {
                stringBuilder.append("cout << " + symbol.getDisplayText() + " << endl;");
            } else {
                throw new IllegalFunctionParameter();
            }
        }

        return stringBuilder.toString();
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
